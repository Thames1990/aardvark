package de.uni_marburg.mathematik.ds.serval.controller

import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ca.allanwang.kau.utils.visibleIf
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.util.distanceToString
import de.uni_marburg.mathematik.ds.serval.util.timeToString
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.event_row.view.*
import java.util.*

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
        location_time.text = timeDifference.timeToString(containerView.context)
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

}
