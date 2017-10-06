package de.uni_marburg.mathematik.ds.serval.controller

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.visibleIf
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.event_row.view.*
import java.util.*
import java.util.concurrent.TimeUnit

/** [View holder][RecyclerView.ViewHolder] for [events][Event]. */
class EventViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    /**
     * Binds an [event] to [views][View].
     *
     * @param event Event to bind to [views][View]
     * @param listener [Click listener lambda][View.OnClickListener]
     */
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
        event_time.text = timeDifference.timeToString(containerView.context)
    }

    private fun Event.displayLocation() {
        location_icon.visibleIf(MainActivity.lastLocation != null)
        location_text.visibleIf(MainActivity.lastLocation != null)

        MainActivity.lastLocation?.let {
            val icon = ContextCompat.getDrawable(containerView.context, R.drawable.location)
            icon.setColorFilter(
                    ContextCompat.getColor(containerView.context, R.color.icon_mute),
                    PorterDuff.Mode.SRC_IN
            )
            location_icon.setImageDrawable(icon)

            val distance = location.distanceTo(MainActivity.lastLocation)
            location_text.text = distance.distanceToString(containerView.context)
        }
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

    private fun Long.timeToString(context: Context): String {
        when {
            TimeUnit.MILLISECONDS.toMinutes(this) < 60 ->
                return String.format(
                        Locale.getDefault(),
                        context.getString(R.string.minutes_ago),
                        TimeUnit.MILLISECONDS.toMinutes(this)
                )
            TimeUnit.MILLISECONDS.toHours(this) < 24 ->
                return String.format(
                        Locale.getDefault(),
                        context.getString(R.string.hours_ago),
                        TimeUnit.MILLISECONDS.toHours(this)
                )
            TimeUnit.MILLISECONDS.toDays(this) < 30 ->
                return String.format(
                        Locale.getDefault(),
                        context.getString(R.string.days_ago),
                        TimeUnit.MILLISECONDS.toDays(this)
                )
            TimeUnit.MILLISECONDS.toDays(this) < 365 ->
                return String.format(
                        Locale.getDefault(),
                        context.getString(R.string.months_ago),
                        TimeUnit.MILLISECONDS.toDays(this).rem(30)
                )
            else -> return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.years_ago),
                    TimeUnit.MILLISECONDS.toDays(this).rem(365)
            )
        }
    }

    private fun Float.distanceToString(context: Context): String = if (this < 1000) {
        String.format(Locale.getDefault(), context.string(R.string.distance_in_meter), this)
    } else {
        String.format(
                Locale.getDefault(),
                context.string(R.string.distance_in_kilometer),
                this.div(1000)
        )
    }

}
