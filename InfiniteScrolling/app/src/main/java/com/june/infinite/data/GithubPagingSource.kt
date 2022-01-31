package com.june.infinite.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.june.infinite.api.GithubService
import com.june.infinite.api.IN_QUALIFIER
import com.june.infinite.data.GithubRepository.Companion.NETWORK_PAGE_SIZE
import com.june.infinite.model.Repo
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

/**
 * PagingSource는 데이터 소스를 정의하고, 이 소스에서 데이터를 가져오는 방법 정의
 * PagingSource를 빌드하기 위해서는 다음 항목을 정의해야 함
 * 1. 패키징 키의 타입 → Int
 * 2. 로드된 데이터의 타입 → Repo
 * 3. 데이터를 가져오는 위치 → GithubService (쿼리 정보도 함께 전달해야 함)
 *
 * RemoteMediator를 정의하면서 필요 없어짐...
 */
class GithubPagingSource(
    private val service: GithubService,
    private val query: String
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val apiQuery = query + IN_QUALIFIER
        return try {
            val response = service.searchRepos(apiQuery, position, params.loadSize)
            val repos = response.items
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    // RefreshKey는 load()의 후속 새로고침 호출에 사용 (초기 로드 다음 호출)
    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? {
        // 가장 최근에 접근했던 index에 가장 가까운 page의 이전 key(또는 previous가 null이라면 next key)를 얻어야 함
        // Anchor position은 가장 최근 accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}