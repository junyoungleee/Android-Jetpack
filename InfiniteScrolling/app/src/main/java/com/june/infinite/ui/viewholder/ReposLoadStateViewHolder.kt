package com.june.infinite.ui.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.june.infinite.R
import com.june.infinite.databinding.ReposLoadStateFooterViewItemBinding

/**
 * Paging LoadState를 기반으로 진행률 표시줄 또는 오류/재시도 버튼을 표시하는 뷰만 포함된 목록
 * 머리글/바닥글 구현 → LoadStateAdapter 사용
 *     RecyclerView.Adapter 구현에서 로드 상태가 변경되면 자동으로 알림받음
 *     Loading/Error 상태에서만 항목만 표시됨
 *     LoadState에 따라 항목이 삭제/삽입/변경되면 RecyclerView에 알림
 * 재시도 매커니즘 → adapter.retry()
 *     내부적으로 이 메서드에서 PagingSource 구현을 호출함
 *     응답은 Flow<PagingData>를 통해 자동으로 전파
 */
class ReposLoadStateViewHolder(
    private val binding: ReposLoadStateFooterViewItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvLoadErrorMsg.text = loadState.error.localizedMessage
        }
        binding.loadProgressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.tvLoadErrorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): ReposLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.repos_load_state_footer_view_item, parent, false)
            val binding = ReposLoadStateFooterViewItemBinding.bind(view)
            return ReposLoadStateViewHolder(binding, retry)
        }
    }
}