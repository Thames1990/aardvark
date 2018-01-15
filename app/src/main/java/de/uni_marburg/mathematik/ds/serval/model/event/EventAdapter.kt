package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.paging.PagedListAdapter
import android.support.v7.recyclerview.extensions.DiffCallback
import android.view.ViewGroup

class EventAdapter(
        private val listener: (Event) -> Unit
) : PagedListAdapter<Event, EventViewHolder>(diffCallback) {

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bindTo(getItem(position), listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder =
            EventViewHolder(parent)

    companion object {
        private val diffCallback = object : DiffCallback<Event>() {
            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean =
                    oldItem.id == newItem.id

        }
    }
}