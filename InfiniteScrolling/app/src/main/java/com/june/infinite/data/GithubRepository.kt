package com.june.infinite.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.june.infinite.api.GithubService
import com.june.infinite.api.IN_QUALIFIER
import com.june.infinite.db.RepoDao
import com.june.infinite.db.RepoDatabase
import com.june.infinite.model.Repo
import com.june.infinite.model.RepoSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import retrofit2.HttpException
import java.io.IOException

// GitHub page API is 1 based: https://developer.github.com/v3/#pagination
private const val GITHUB_STARTING_PAGE_INDEX = 1

/**
 * Repository class that works with local and remote data sources.
 * Paging 라이브러리에서 처리되는 데이터 소스와 관련한 다양한 작업 실행
 *    1. 메모리 내 캐시를 처리
 *    2. 사용자가 목록의 끝에 가까워지면 데이터 요청
 */
class GithubRepository(
    private val service: GithubService,
    private val database: RepoDatabase
    ) {

    /**
     * Search repositories whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<Repo>> {

        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory =  {
            // PagingSource 객체를 반환하는 쿼리 메서드
            database.reposDao().reposByName(dbQuery)
        }

        @OptIn(ExperimentalPagingApi::class)
        val pager = Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = GithubRemoteMediator(query, service, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow

        return pager
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 50
    }
}