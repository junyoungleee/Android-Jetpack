package com.june.infinite.ui.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.june.infinite.ui.viewholder.ReposLoadStateViewHolder

/**
 * LoadStateAdapter를 확장하는 Adapter
 *     구성될 때 재시도 함수가 ViewHolder에 전달되므로 재시도 함수를 매개변수로 수신
 *     Adapter의 두 함수 모두에 LoadState를 전달함
 */
class ReposLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<ReposLoadStateViewHolder>() {
    // ViewHolder를 바인딩
    override fun onBindViewHolder(holder: ReposLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    // 상위 ViewGroup 및 재시도 함수를 기반을 ReposLoadStateViewHolder를 만드는 방법을 정의
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ReposLoadStateViewHolder {
        return ReposLoadStateViewHolder.create(parent, retry)
    }
}