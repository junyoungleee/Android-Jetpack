package com.june.infinite.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.june.infinite.data.GithubRepository
import com.june.infinite.model.Repo
import com.june.infinite.model.RepoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel
 * Paging 3.0을 사용하면 Flow를 LiveData로 변환하지 않아도 됨
 *    기존 → LiveData<RepoSearchResult>
 *    Paging 3.0 → Flow<PagingData<Repo>>
 *        Flow에서 map 또는 filter와 같은 작업을 실행하는 경우,
 *        실행한 후 cachedIn()을 호출하여 작업을 다시 트리거할 필요가 없도록 해야 함
 */
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<Repo>>? = null

    fun searchRepo(queryString: String): Flow<PagingData<Repo>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString
        val newResult: Flow<PagingData<Repo>> = repository.getSearchResultStream(queryString) // 새 검색어가 다른 경우 호출
            .cachedIn(viewModelScope) // CoroutineScope에서 Flow<PagingData>의 콘텐츠를 캐시할 수 있는 메서드
        currentSearchResult = newResult
        return newResult
    }
}