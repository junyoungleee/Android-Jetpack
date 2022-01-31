package com.june.infinite.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.june.infinite.model.Repo

/**
 * GithubRemoteMediator 및 PagingSource를 구현함
 */
@Dao
interface RepoDao {

    // Repo 객체 리스트 삽입(중복 시, 객체를 대체함)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Repo>)

    // 결과를 별 수로 내림차순 정렬, 이름 가나다순 정렬
    @Query("SELECT * FROM repos WHERE " +
            "name LIKE :queryString OR description LIKE :queryString " +
            "ORDER BY stars DESC, name ASC")
    fun reposByName(queryString: String): PagingSource<Int, Repo>

    // 모든 데이터 삭제
    @Query("DELETE FROM repos")
    suspend fun clearRepos()
}