package com.example.wallpaper.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class PaginationScrollListener(layoutManager: GridLayoutManager?): RecyclerView.OnScrollListener() {
    var layoutManager: GridLayoutManager? = null

    init {
        this.layoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount: Int = layoutManager!!.childCount
        val totalItemCount: Int = layoutManager!!.itemCount
        val firstVisibleItemPosition: Int = layoutManager!!.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >=
                    totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun getTotalPageCount(): Int
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean
}