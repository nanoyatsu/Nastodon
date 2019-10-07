package com.nanoyatsu.layoutComponent

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class InfiniteScrollListener(private val layoutManager: RecyclerView.LayoutManager) :
    RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var currentPage = 1

    override fun onScrolled(parent: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(parent, dx, dy)

        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem =
            when (layoutManager) {
                is GridLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
                else -> 0
            }

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        } else {
            if (totalItemCount - parent.childCount <= firstVisibleItem) {
                loading = true
                currentPage++
                onLoadMore(currentPage)
            }
        }
    }

    internal abstract fun onLoadMore(current_page: Int)
}