package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventComparator

/** [Adapter][RecyclerView.Adapter] for [events][Event] */
class EventAdapter(val events: MutableList<Event>, private val listener: (Event) -> Unit) :
        RecyclerView.Adapter<EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventViewHolder(parent.inflate(R.layout.event_row))

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
            holder.bind(events[position], listener)

    override fun getItemCount(): Int = events.size

    fun sort(comparator: EventComparator, reversed: Boolean = false, location: Location? = null) {
        when (comparator) {
            EventComparator.Distance    -> events.sortBy { it.location.distanceTo(location) }
            EventComparator.Measurement -> events.sortBy { it.measurements.size }
            EventComparator.Time        -> events.sortBy { it.time }
        }
        if (reversed) {
            events.reverse()
        }
        notifyDataSetChanged()
    }
}
