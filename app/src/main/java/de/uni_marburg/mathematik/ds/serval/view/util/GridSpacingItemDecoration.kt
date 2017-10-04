package de.uni_marburg.mathematik.ds.serval.view.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.view.View

/** Adds delimeters between grids. */
class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State?) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        with(outRect) {
            if (includeEdge) {
                left = spacing - column * spacing / spanCount
                right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    top = spacing
                }

                bottom = spacing
            } else {
                left = column * spacing / spanCount
                right = spacing - (column + 1) * spacing / spanCount

                if (position >= spanCount) {
                    top = spacing
                }
            }
        }
    }
}
