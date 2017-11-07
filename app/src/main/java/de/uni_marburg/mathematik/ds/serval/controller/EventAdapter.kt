package de.uni_marburg.mathematik.ds.serval.controller

import android.Manifest
import android.arch.lifecycle.Observer
import android.graphics.PorterDuff
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.BuildConfig
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
 * @param activity Calling activity
 * @param listener Click listener
 */
class EventAdapter(
        private val activity: FragmentActivity,
        private val listener: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(), AutoUpdatableAdapter {

    /**
     * The last known location.
     *
     * This is initialized as an empty [Location] to avoid nullability.
     */
    private var lastLocation: Location = Location(BuildConfig.APPLICATION_ID)

    /**
     * [Events][Event] tracked by this adapter. The adapter is
     * [automatically notified][AutoUpdatableAdapter.autoNotify] about changes and updates the UI
     * accordingly.
     *
     * This will be moved to a Room database.
     */
    private var events: MutableList<Event> by Delegates.observable(mutableListOf()) { _, old, new ->
        autoNotify(old, new) { event1, event2 -> event1.time == event2.time }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        LocationLiveData(activity).observe(activity, Observer<Location> { location ->
            location?.let { lastLocation -> this.lastLocation = lastLocation }
        })
        return EventViewHolder(parent.inflate(R.layout.event_row), lastLocation)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
            holder.bindTo(events[position], listener)

    override fun getItemCount(): Int = events.size

    /** Loads [events][Event] from the API server. **/
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
        if (reversed) {
            when (comparator) {
                EventComparator.Distance    -> events.sortByDescending { it.location.distanceTo(lastLocation) }
                EventComparator.Measurement -> events.sortByDescending { it.measurements.size }
                EventComparator.Time        -> events.sortByDescending { -it.time }
            }
        } else {
            when (comparator) {
                EventComparator.Distance    -> events.sortBy { it.location.distanceTo(lastLocation) }
                EventComparator.Measurement -> events.sortBy { it.measurements.size }
                EventComparator.Time        -> events.sortBy { -it.time }
            }
        }
        notifyDataSetChanged()
    }

    /**
     * View holder for [events][Event].
     *
     * @param containerView Inflated view
     * @param lastLocation Last known location
     * */
    class EventViewHolder(
            override val containerView: View,
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
        private fun Event.displayLocation() =
                with(containerView.context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    location_icon.visibleIf(this)
                    location_text.visibleIf(this)

                    if (this) {
                        val icon = containerView.context.drawable(R.drawable.location)
                        icon.setColorFilter(
                                containerView.context.color(R.color.icon_mute),
                                PorterDuff.Mode.SRC_IN
                        )
                        location_icon.setImageDrawable(icon)
                        location_text.text = location.distanceTo(lastLocation).distanceToString()
                    }
                }

        /** Displays the measurements of the [event][Event]. **/
        private fun Event.displayMeasurementTypes() {
            measurement_types.removeAllViews()
            measurements.toHashSet().forEach { measurement ->
                val icon = ImageView(containerView.context)
                icon.setImageResource(measurement.type.resId)
                icon.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
                measurement_types.addView(icon)
            }
        }

        /** Converts UNIX time to human readable information in relation to the current time **/
        private fun Long.timeToString(): String {
            val format: String
            val value: Long

            when {
                TimeUnit.MILLISECONDS.toMinutes(this) < 60 -> {
                    format = containerView.context.string(R.string.minutes_ago)
                    value = TimeUnit.MILLISECONDS.toMinutes(this)
                }
                TimeUnit.MILLISECONDS.toHours(this) < 24   -> {
                    format = containerView.context.string(R.string.hours_ago)
                    value = TimeUnit.MILLISECONDS.toHours(this)
                }
                TimeUnit.MILLISECONDS.toDays(this) < 30    -> {
                    format = containerView.context.string(R.string.days_ago)
                    value = TimeUnit.MILLISECONDS.toDays(this)
                }
                TimeUnit.MILLISECONDS.toDays(this) < 365   -> {
                    format = containerView.context.string(R.string.months_ago)
                    value = TimeUnit.MILLISECONDS.toDays(this).rem(30)
                }
                else                                       -> {
                    format = containerView.context.string(R.string.years_ago)
                    value = TimeUnit.MILLISECONDS.toDays(this).rem(365)
                }
            }

            return String.format(format, value)
        }

        /** Converts distance in meters **/
        private fun Float.distanceToString(): String =
                if (this < 1000) String.format(
                        containerView.context.string(R.string.distance_in_meter),
                        this
                ) else String.format(
                        containerView.context.string(R.string.distance_in_kilometer),
                        this.div(1000)
                )

    }
}