package de.uni_marburg.mathematik.ds.serval.controller

import android.arch.lifecycle.Observer
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
import de.uni_marburg.mathematik.ds.serval.model.location.LocationLiveData
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * Adapter for [events][Event]
 *
 * @param fragmentActivity Calling context
 * @param listener Click listener
 */
class EventAdapter(
        private val fragmentActivity: FragmentActivity,
        private val listener: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(), AutoUpdatableAdapter {

    /**
     * The last known location.
     *
     * This is initialized as an empty [Location] to avoid nullability.
     */
    private var lastLocation: Location = Location("")

    /**
     * [Events][Event] tracked by this adapter. The adapter is
     * [automatically notified][AutoUpdatableAdapter.autoNotify] about changes and updates the UI
     * accordingly.
     *
     * This will be moved to a Room database.
     */
    private var events: List<Event> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { event1, event2 -> event1.time == event2.time }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        LocationLiveData(fragmentActivity.applicationContext).observe(
                fragmentActivity,
                Observer<Location> { it?.let { lastLocation = it } }
        )
        // TODO Figure out how to use Kotlin Android extensions view caching with Kotlin inner class
        return EventViewHolder(parent.inflate(R.layout.event_row), fragmentActivity, lastLocation)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
            holder.bindTo(events[position], listener)

    override fun getItemCount(): Int = events.size

    fun loadEvents() {
        events = EventProvider.load()
    }

    /**
     * Sorts [events] by a [distance][EventComparator.Distance], [time][EventComparator.Time] or
     * [amount of measurements][EventComparator.Measurement].
     *
     * @param comparator [Event] comparator
     * @param reversed If true, sorts descending; ascending otherwise.
     */
    fun sortBy(comparator: EventComparator, reversed: Boolean = false) {
        events = when (comparator) {
            EventComparator.Distance ->
                events.sortedBy {
                    if (reversed) -it.location.distanceTo(lastLocation)
                    else it.location.distanceTo(lastLocation)
                }
            EventComparator.Measurement ->
                events.sortedBy {
                    if (reversed) -it.measurements.size
                    else it.measurements.size
                }
            EventComparator.Time ->
                events.sortedBy {
                    if (reversed) it.time
                    else -it.time
                }
        }
    }

    /**
     * View holder for [events][Event].
     *
     * @param containerView Inflated view
     * @param fragmentActivity Calling context
     * @param lastLocation Last known location
     * */
    class EventViewHolder(
            override val containerView: View,
            private val fragmentActivity: FragmentActivity,
            private val lastLocation: Location
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        /**
         * Binds [event] to a view and a click listener.
         *
         * @param event Event to bind
         * @param listener Click listener
         */
        fun bindTo(event: Event, listener: (Event) -> Unit) = with(event) {
            displayTime()
            displayLocation()
            displayMeasurementTypes()
            containerView.setOnClickListener { listener(this) }
        }

        /** Displays the time of the [event][Event]. */
        private fun Event.displayTime() {
            val timeDifference = Calendar.getInstance().timeInMillis - time
            event_time.text = timeDifference.timeToString()
        }

        /**
         * Displays the location of the event in relation to [the last known location][lastLocation]
         **/
        private fun Event.displayLocation() {
            val icon = ContextCompat.getDrawable(fragmentActivity, R.drawable.location)
            icon.setColorFilter(fragmentActivity.color(R.color.icon_mute), PorterDuff.Mode.SRC_IN)
            location_icon.setImageDrawable(icon)
            location_text.text = location.distanceTo(lastLocation).distanceToString()
        }

        /** Displays the measurements of the [event][Event]. **/
        private fun Event.displayMeasurementTypes() {
            measurement_types.removeAllViews()
            measurements.mapTo(HashSet()) { it.type }.forEach {
                val icon = ImageView(fragmentActivity)
                icon.setImageResource(it.getResId(fragmentActivity))
                icon.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
                measurement_types.addView(icon)
            }
        }

        /** Converts UNIX time to human readable information in relation to the current time **/
        private fun Long.timeToString(): String = when {
            TimeUnit.MILLISECONDS.toMinutes(this) < 60 -> String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(this)
            )
            TimeUnit.MILLISECONDS.toHours(this) < 24 -> String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.hours_ago),
                    TimeUnit.MILLISECONDS.toHours(this)
            )
            TimeUnit.MILLISECONDS.toDays(this) < 30 -> String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.days_ago),
                    TimeUnit.MILLISECONDS.toDays(this)
            )
            TimeUnit.MILLISECONDS.toDays(this) < 365 -> String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.months_ago),
                    TimeUnit.MILLISECONDS.toDays(this).rem(30)
            )
            else -> String.format(
                    Locale.getDefault(),
                    fragmentActivity.string(R.string.years_ago),
                    TimeUnit.MILLISECONDS.toDays(this).rem(365)
            )
        }

        /** Converts distance in meters in relation to [lastLocation] **/
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