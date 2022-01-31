package com.june.infinite.ui

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
         outRect.bottom = spaceHeight
    }
}