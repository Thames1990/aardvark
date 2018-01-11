package de.uni_marburg.mathematik.ds.serval.controller

import android.Manifest
import android.arch.lifecycle.Observer
import android.graphics.PorterDuff
import android.location.Location
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.setIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.model.location.LocationLiveData
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.distanceToString
import de.uni_marburg.mathematik.ds.serval.utils.timeToString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import java.util.*
import kotlin.properties.Delegates
import kotlin.with

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
    // TODO Save last location information in SharedPreferences
    private var lastLocation: Location = Location(BuildConfig.APPLICATION_ID)

    /**
     * [Events][Event] tracked by this adapter. The adapter is
     * [automatically notified][AutoUpdatableAdapter.autoNotify] about changes and updates the UI
     * accordingly.
     *
     * This will be moved to a Room database.
     */
    var events: List<Event> by Delegates.observable(emptyList()) { _, old, new ->
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

    /**
     * Sorts [events] by a [distance][EventComparator.DISTANCE], [time][EventComparator.TIME] or
     * [amount of measurements][EventComparator.MEASUREMENT].
     *
     * @param comparator [Event] comparator
     * @param reversed If true, sorts descending; ascending otherwise.
     */
    fun sortEventsBy(comparator: EventComparator, reversed: Boolean = false) {
        events = events.sortedWith(compareBy {
            if (reversed) when (comparator) {
                DISTANCE -> -it.location.distanceTo(lastLocation)
                MEASUREMENT -> -it.measurements.size
                TIME -> -it.time
            } else when (comparator) {
                DISTANCE -> it.location.distanceTo(lastLocation)
                MEASUREMENT -> it.measurements.size
                TIME -> it.time
            }
        })
    }

    /**
     * View holder for [events][Event].
     *
     * @param containerView Inflated view
     * @param lastLocation Last known location
     */
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
            containerView.setBackgroundColor(Prefs.backgroundColor)
            containerView.setOnClickListener { listener(this) }
        }

        /** Displays the time of the [event][Event]. */
        private fun Event.displayTime() {
            val timeDifference = Calendar.getInstance().timeInMillis - time
            event_time.apply {
                text = timeDifference.timeToString(containerView.context)
                setTextColor(Prefs.textColor)
            }
        }

        /**
         * Displays the location of the event in relation to [the last known location][lastLocation]
         * of the current device.
         **/
        private fun Event.displayLocation() = with(containerView.context) {
            val hasLocationPermission = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            if (hasLocationPermission) {
                location_icon.setIcon(
                        icon = GoogleMaterial.Icon.gmd_my_location,
                        color = Prefs.iconColor
                )
                location_text.apply {
                    text = location.distanceTo(lastLocation).distanceToString(this@with)
                    setTextColor(Prefs.textColor)
                }
            } else {
                location_icon.gone()
                location_text.gone()
                val params = guideline.layoutParams as ConstraintLayout.LayoutParams
                params.guideEnd = 0
                guideline.layoutParams = params
            }
        }

        /** Displays the measurements of the [event][Event]. **/
        private fun Event.displayMeasurementTypes() {
            measurement_types.removeAllViews()
            measurements.toHashSet().forEach { measurement ->
                measurement_types.addView(ImageView(containerView.context).apply {
                    setImageResource(measurement.type.iconRes)
                    setColorFilter(Prefs.iconColor, PorterDuff.Mode.SRC_IN)
                    layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
                })
            }
        }

    }
}