package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.location.Location
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder
import de.uni_marburg.mathematik.ds.serval.model.comparators.LocationComparator
import de.uni_marburg.mathematik.ds.serval.model.comparators.MeasurementsComparator
import de.uni_marburg.mathematik.ds.serval.model.comparators.TimeComparator
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator
import java.util.*

/** [Adapter][RecyclerView.Adapter] for [events][Event] */
class EventAdapter(events: List<Event>) : RecyclerView.Adapter<EventViewHolder>() {

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

    /** Filters [events][Event] based on their measurements or time. */
    fun sort(comparator: EventComparator, reversed: Boolean) {
        when (comparator) {
            EventComparator.MEASUREMENTS -> {
                val measurementsComparator = MeasurementsComparator()
                if (reversed) {
                    events.sortedWith(measurementsComparator).reversed()
                } else {
                    events.sortedWith(measurementsComparator)
                }
            }
            EventComparator.SHUFFLE -> Collections.shuffle(events)
            EventComparator.TIME -> {
                val timeComparator = TimeComparator()
                if (reversed) {
                    events.sortedWith(timeComparator).reversed()
                } else {
                    events.sortedWith(timeComparator)
                }
            }
            else -> return
        }
        notifyDataSetChanged()
    }

    /** Filters [events][Event] based on their location compared to an [origin]. */
    fun sort(comparator: EventComparator, reversed: Boolean, origin: Location) {
        when (comparator) {
            EventComparator.DISTANCE -> {
                val locationComparator = LocationComparator(origin)
                if (reversed) {
                    events.sortedWith(locationComparator).reversed()
                } else {
                    events.sortedWith(locationComparator)
                }
            }
            else -> return
        }
        notifyDataSetChanged()
    }
}
