package com.june.infinite.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.june.infinite.Injection
import com.june.infinite.databinding.ActivityMainBinding
import com.june.infinite.ui.adapter.ReposAdapter
import com.june.infinite.ui.adapter.ReposLoadStateAdapter
import com.june.infinite.viewmodel.SearchRepositoriesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SearchRepositoriesViewModel
    private val adapter =  ReposAdapter()

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this, this))
            .get(SearchRepositoriesViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = VerticalSpaceItemDecoration(25)
        binding.rcRepositories.addItemDecoration(decoration)

        initRecyclerView()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)

        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, binding.searchBar.text.trim().toString())
    }


    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(query).collect {
                adapter.submitData(it)
            }
        }
    }

    private fun initRecyclerView() {
        // withLoadStateHeader: ???????????? ??????????????? ??????, ????????? ?????? ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ?????????.
        // withLoadStateFooter: ???????????? ??????????????? ??????, ????????? ??? ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ?????????.
        // withLoadStateHeaderAndFooter: ???????????? ???????????? ??????????????? ??????, ????????? ??? ???????????? ?????? ???????????? ??? ?????? ??????.
        binding.rcRepositories.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ReposLoadStateAdapter { adapter.retry() },
            footer = ReposLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds.
            binding.rcRepositories.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.loadProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails.
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.rcRepositories.layoutManager = LinearLayoutManager(this)
    }


    private fun initSearch(query: String) {
        binding.searchBar.setText(query)

        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        binding.searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }

        // ?????????????????? ????????? ?????? ????????? ????????? ?????????
        lifecycleScope.launch {
            adapter.loadStateFlow
                // RemoteMediator??? ?????? REFRESH LoadState??? ????????? ?????? ?????????
                .distinctUntilChangedBy { it.refresh }
                // Remote REFRESH??? ????????? ??????(EX. NotLoading)?????? ?????????
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.rcRepositories.scrollToPosition(0) } // ????????? ?????? ?????????
        }
    }

    // viewModel ??? adapter??? ????????? search()??? ??????
    private fun updateRepoListFromInput() {
        binding.searchBar.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.rcRepositories.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.rcRepositories.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}