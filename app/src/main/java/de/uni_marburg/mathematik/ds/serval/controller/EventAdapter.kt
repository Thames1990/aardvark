package de.uni_marburg.mathematik.ds.serval.controller

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.EventProvider
import kotlin.properties.Delegates

/**
 * [Adapter][RecyclerView.Adapter] for [events][Event].
 *
 * Is able to sort [events][Event] based on their [occurence time][Event.time],
 * [location][Event.location] or [measurement][Event.measurements].
 *
 * @param listener Click listener lambda
 * @property events Events bound by this adapter
 */
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

    /** Loads [events]. */
    fun loadEvents() {
        events = EventProvider.generate()
    }

    /**
     * Sorts [events] based on their [occurence time][Event.time], [location][Event.location]
     * or [measurement][Event.measurements].
     *
     * @param comparator Determines how to sort the vents
     * @param reversed Determines whether sorting should be reversed (descending)
     * @param location Location of the event. Is only useful for a [time comparator]
     * [EventComparator.Time].
     */
    fun sort(comparator: EventComparator, reversed: Boolean = false, location: Location? = null) {
        events = when (comparator) {
            EventComparator.Distance ->
                if (reversed) events.sortedBy { -it.location.distanceTo(location) }
                else events.sortedBy { it.location.distanceTo(location) }
            EventComparator.Measurement ->
                if (reversed) events.sortedBy { -it.measurements.size }
                else events.sortedBy { it.measurements.size }
            EventComparator.Time ->
                if (reversed) events.sortedBy { it.time }
                else events.sortedBy { -it.time }
        }
        notifyDataSetChanged()
    }
}