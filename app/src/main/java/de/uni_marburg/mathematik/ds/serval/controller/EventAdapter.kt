package de.uni_marburg.mathematik.ds.serval.controller

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.PorterDuff
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.event.EventProvider
import de.uni_marburg.mathematik.ds.serval.model.location.LocationViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.event_row.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class EventAdapter(
        private val fragmentActivity: FragmentActivity,
        private val listener: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(), AutoUpdatableAdapter {

    private var events: List<Event> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { event1, event2 -> event1.time == event2.time }
    }

    var lastLocation: Location? = null

    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProviders.of(fragmentActivity).get(LocationViewModel::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        locationViewModel.location.observe(fragmentActivity, Observer<Location> {
            lastLocation = it
        })
        return EventViewHolder(parent.inflate(R.layout.event_row))
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
            holder.bind(events[position], listener)

    override fun getItemCount(): Int = events.size

    fun loadEvents(generated: Boolean = false) {
        events = if (generated) EventProvider.generate() else EventProvider.load()
    }

    fun sortBy(comparator: EventComparator, reversed: Boolean = false, location: Location? = null) {
        events = when (comparator) {
            EventComparator.Distance ->
                if (reversed) events.sortedBy { -it.location.distanceTo(lastLocation) }
                else events.sortedBy { it.location.distanceTo(lastLocation) }
            EventComparator.Measurement ->
                if (reversed) events.sortedBy { -it.measurements.size }
                else events.sortedBy { it.measurements.size }
            EventComparator.Time ->
                if (reversed) events.sortedBy { it.time }
                else events.sortedBy { -it.time }
        }
        notifyDataSetChanged()
    }

    inner class EventViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(event: Event, listener: (Event) -> Unit) = with(containerView) {
            with(event) {
                displayTime()
                displayLocation()
                displayMeasurementTypes()
                setOnClickListener { listener(this) }
            }
        }

        private fun Event.displayTime() {
            val timeDifference = Calendar.getInstance().timeInMillis - time
            event_time.text = timeDifference.timeToString()
        }

        private fun Event.displayLocation() {
            val icon = ContextCompat.getDrawable(fragmentActivity, R.drawable.location)
            icon.setColorFilter(fragmentActivity.color(R.color.icon_mute), PorterDuff.Mode.SRC_IN)
            location_icon.setImageDrawable(icon)
            location_text.text = location.distanceTo(lastLocation).distanceToString()
        }

        private fun Event.displayMeasurementTypes() {
            measurement_types.removeAllViews()
            measurements.mapTo(HashSet()) { it.type }.forEach {
                val icon = ImageView(itemView.context)
                icon.setImageResource(it.getResId(itemView.context))
                icon.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
                itemView.measurement_types.addView(icon)
            }
        }

        private fun Long.timeToString(): String {
            when {
                TimeUnit.MILLISECONDS.toMinutes(this) < 60 ->
                    return String.format(
                            Locale.getDefault(),
                            fragmentActivity.getString(R.string.minutes_ago),
                            TimeUnit.MILLISECONDS.toMinutes(this)
                    )
                TimeUnit.MILLISECONDS.toHours(this) < 24 ->
                    return String.format(
                            Locale.getDefault(),
                            fragmentActivity.getString(R.string.hours_ago),
                            TimeUnit.MILLISECONDS.toHours(this)
                    )
                TimeUnit.MILLISECONDS.toDays(this) < 30 ->
                    return String.format(
                            Locale.getDefault(),
                            fragmentActivity.getString(R.string.days_ago),
                            TimeUnit.MILLISECONDS.toDays(this)
                    )
                TimeUnit.MILLISECONDS.toDays(this) < 365 ->
                    return String.format(
                            Locale.getDefault(),
                            fragmentActivity.getString(R.string.months_ago),
                            TimeUnit.MILLISECONDS.toDays(this).rem(30)
                    )
                else -> return String.format(
                        Locale.getDefault(),
                        fragmentActivity.getString(R.string.years_ago),
                        TimeUnit.MILLISECONDS.toDays(this).rem(365)
                )
            }
        }

        private fun Float.distanceToString(): String = if (this < 1000) {
            String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.distance_in_meter),
                    this
            )
        } else {
            String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.distance_in_kilometer),
                    this.div(1000)
            )
        }

    }
}