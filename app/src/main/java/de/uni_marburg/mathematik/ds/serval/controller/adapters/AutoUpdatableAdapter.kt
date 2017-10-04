package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

interface AutoUpdatableAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotify(
            old: List<T>,
            new: List<T>,
            compare: (T, T) -> Boolean
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    compare(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    old[oldItemPosition] == new[newItemPosition]

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size
        })

        diff.dispatchUpdatesTo(this)
    }
}