package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.EventProvider
import kotlin.properties.Delegates

/** [Adapter][RecyclerView.Adapter] for [events][Event] */
class EventAdapter(private val listener: (Event) -> Unit) :
        RecyclerView.Adapter<EventViewHolder>(), AutoUpdatableAdapter {

    var events: List<Event> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { event1, event2 -> event1.time == event2.time }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            EventViewHolder(parent.inflate(R.layout.event_row))

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
            holder.bind(events[position], listener)

    override fun getItemCount(): Int = events.size

    fun loadEvents() {
        events = EventProvider.generate()
    }

    fun sort(comparator: EventComparator, reversed: Boolean = false, location: Location? = null) {
        when (comparator) {
            EventComparator.Distance ->
                events = if (reversed) {
                    events.sortedBy { -it.location.distanceTo(location) }
                } else {
                    events.sortedBy { it.location.distanceTo(location) }
                }
            EventComparator.Measurement ->
                events = if (reversed) {
                    events.sortedBy { -it.measurements.size }
                } else {
                    events.sortedBy { it.measurements.size }
                }
            EventComparator.Time ->
                events = if (reversed) {
                    events.sortedBy { it.time }
                } else {
                    events.sortedBy { -it.time }
                }
        }
        notifyDataSetChanged()
    }
}