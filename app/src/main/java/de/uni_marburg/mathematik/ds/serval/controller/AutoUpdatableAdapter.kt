package de.uni_marburg.mathematik.ds.serval.controller

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

/** Extension for [RecyclerView.Adapter] to automatically notify about content changes. */
interface AutoUpdatableAdapter {

    /**
     * Automatically notifies [DiffUtil] to update the [RecyclerView] if [new] content is different
     * from [old] content. This is determined by [compare].
     *
     * @param T The type of content
     * @param old Old content
     * @param new New content
     * @param compare Compares content
     */
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