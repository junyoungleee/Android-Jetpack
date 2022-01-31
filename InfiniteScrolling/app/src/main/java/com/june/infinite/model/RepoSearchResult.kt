package com.june.infinite.model

import java.lang.Exception

/**
 * RepoSearchResult에서는 성공/오류 사례 캡슐화
 * Paging 3.0에서는 LoadResult로 성공/오류 사례를 모두 모데링함
 *   성공 → 저장소 데이터가 포함됨
 *   오류 → Exception의 이유 포함됨
 */
sealed class RepoSearchResult {
    data class Success(val data: List<Repo>) : RepoSearchResult()
    data class Error(val error: Exception) : RepoSearchResult()
}