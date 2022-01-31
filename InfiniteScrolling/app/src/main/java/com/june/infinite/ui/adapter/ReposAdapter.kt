package com.june.infinite.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.june.infinite.model.Repo
import com.june.infinite.ui.viewholder.RepoViewHolder

/**
 * ReposAdapter ← PagingDataAdapter
 * Paging 콘텐츠가 로드될 때마다 PagingDataAdapter에서 알람을 받은 다음
 * RecyclerView에 업데이트하라는 신호를 보냄
 */
class ReposAdapter : PagingDataAdapter<Repo, RepoViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            holder.bind(repoItem)
        }

    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean =
                oldItem == newItem
        }
    }
}