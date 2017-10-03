package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventComparator

/** [Adapter][RecyclerView.Adapter] for [events][Event] */
class EventAdapter(events: MutableList<Event>) : RecyclerView.Adapter<EventViewHolder>() {

    var events = events
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.performBind(events[position])
    }

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
