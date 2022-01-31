package com.june.infinite.data

import androidx.paging.*
import androidx.room.withTransaction
import com.june.infinite.api.GithubService
import com.june.infinite.api.IN_QUALIFIER
import com.june.infinite.db.RemoteKeys
import com.june.infinite.model.Repo
import com.june.infinite.db.RepoDatabase
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

/**
 * GithubRepository 클래스를 대체하는 클래스 >
 * load() 메서드와 함께 RemoteMediator 추상 클래스를 정의함
 * 네트워크에서 더 많은 데이터를 로드해야 할 때마다 이 메서드가 호출됨
 * 이 클래스는 새 쿼리마다 다시 생성됨
 *
 * 이 클래스는 MediatorResult 객체(둘 중 하나)를 반환함
 *     Error → 네트워크에 데이터를 요청하는 동안 오류가 발생한 경우
 *     Success → 네트워크에서 데이터를 가져온 경우, 여기에서 더 많은 데이터를 로드할 수 있는지 여부를 나타내는 신호도 전달
 *               ex) 네트워크 응답에 성공했지만, 저장소 목록이 비어있으면 더이상 로드할 데이터가 없는 것
 */
@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: GithubService, // 네트워크 요청
    private val repoDatabase: RepoDatabase // 네트워크 요청에서 가져온 데이터 저장
) : RemoteMediator<Int, Repo>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {
        // 네트워크 요청을 작성할 수 있도록 로드 메서드에는 필요한 정보를 모두 제공하는 2개의 매개변수 존재
        // PagingState : 이전에 로드된 페이지, 가장 최근에 액세스한 인덱스, ㅍ이징 스트림을 초기화할 때 정의한 PagingConfig에 관한 정보 제공
        // LoadType : 이전에 로드한 데이터의 끝부분(LoadType.APPEND)
        //                               or 시작부분(LoadType.PREPEND)에서 데이터를 로드해야 하는지
        //                               or 데이터를 처음으로 로드하는지(LoadType.REFRESH)를 나타냄

        // LoadType을 기반으로 네트워크에서 로드해야 하는 페이지를 확인인
        // remoteKeys가 null이면 새로고침 결과가 아직 데이터베이스에 없는 것
       val page = when (loadType) {
            LoadType.REFRESH -> {
                // 데이터를 처음 로드할 때 or PagingDataAdapter.refresh()가 호출되는 경우
                // → 데이터를 로드하기 위한 참조 지점은 state.anchorPosition
                //   → 첫번째 로드인 경우, anchorPosition은 null
                //   → PagingDataAdapter.refresh()가 호출된 경우, anchorPosition이 표시된 목록에 처음으로 표시되는 위치
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                // remoteKey가 null이 아니면 nextKey를 가져올 수 있음
                // GitHub API에서는 페이지 키가 순차적으로 증가함 (따라서 1을 뺌)
                // null이면 초기 페이지 값을 로드
                remoteKeys?.nextKey?.minus(1)  ?: GITHUB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                // 현재 로드된 데이터 세트의 시작부분에서 데이터를 로드해야 하는 경우
                // → 데이터베이스의 첫번째 항목을 기반으로 네트워크 페이지 키를 계산
                val remoteKes = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKes?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKes != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                // 현재 로드된 데이터 세트의 끝 부분에서 데이터를 로드해야 하는 경우
                // → 데이터베이스의 마지막 항목을 기반으로 네트워크 페이지 키를 계산
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }
        val apiQuery = query + IN_QUALIFIER

        try {
            val apiResponse = service.searchRepos(apiQuery, page, state.config.pageSize)
            val repos = apiResponse.items
            val endOfPaginationReached = repos.isEmpty() // 수신한 리포 목록이 비어있는지 확인

            // 코루틴 내에서 데이터베이스 트랜잭션을 실행하기 위한 withTransaction(room-ktx)
            repoDatabase.withTransaction {
                // REFRESH인 경우, 데이터베이스를 지움
                if (loadType == LoadType.REFRESH) {
                    repoDatabase.remoteKeysDao().clearRemoteKeys()
                    repoDatabase.reposDao().clearRepos()
                }

                // 비어있지 않다면, Repo의 RemoteKeys 계산
                val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = repos.map {
                    RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                // RemoteKeys, Repos를 저장함
                repoDatabase.remoteKeysDao().insertAll(keys)
                repoDatabase.reposDao().insertAll(repos)
            }
            // MediatorResult.Success(false) 반환
            // 저장소 목록이 비어있는 경우, MediatorResult.Success(true)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repo>): RemoteKeys? {
        // 아이템이 포함된 마지막으로 검색된 페이지를 가져움
        // 마지막 페이지에서 마지막 아이템을 가져옴
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { repo ->
                // Get the remote keys of the last item retrieved
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repo>): RemoteKeys? {
        // 검색된 아이템이 포함된 첫 페이지를 가져옴
        // 첫번째 페이지에서 첫번째 아이템을 가져옴
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Repo>
    ): RemoteKeys? {
        // Paging 라이브러리가 anchor 위치 다음에 데이터를 로드하려고 시도함
        // anchor 위치에서 가장 가까운 아이템 가져옴
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                repoDatabase.remoteKeysDao().remoteKeysRepoId(repoId)
            }
        }
    }
}