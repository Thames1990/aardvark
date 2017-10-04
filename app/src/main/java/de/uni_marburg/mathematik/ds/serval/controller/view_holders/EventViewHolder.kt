package de.uni_marburg.mathematik.ds.serval.controller.view_holders

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
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.event_row.*
import kotlinx.android.synthetic.main.event_row.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** [View holder][RecyclerView.ViewHolder] for [events][Event] */
class EventViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

    fun bind(event: Event, listener: (Event) -> Unit) = with(containerView) {
        setupTime(event)
        setupLocation(event)
        setupMeasurementIcons(event)
        setOnClickListener { listener(event) }
    }

    private fun setupTime(event: Event) {
        val calendar = Calendar.getInstance()
        val timeDifference = calendar.timeInMillis - event.time
        val format = SimpleDateFormat.getDateInstance(
                DateFormat.MEDIUM,
                Locale.getDefault()
        )
        // TODO Refactor with lambda gorgeousness
        when {
            TimeUnit.MILLISECONDS.toMinutes(timeDifference) < 60 -> time.text = String.format(
                    Locale.getDefault(),
                    containerView.context.string(R.string.time_minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(timeDifference)
            )
            TimeUnit.MILLISECONDS.toHours(timeDifference) < 24 -> time.text = String.format(
                    Locale.getDefault(),
                    containerView.context.string(R.string.time_hours_ago),
                    TimeUnit.MILLISECONDS.toHours(timeDifference)
            )
            TimeUnit.MILLISECONDS.toDays(timeDifference) < 7 -> time.text = String.format(
                    Locale.getDefault(),
                    containerView.context.string(R.string.time_days_ago),
                    TimeUnit.MILLISECONDS.toDays(timeDifference)
            )
            else -> time.text = format.format(event.time)
        }
    }

    private fun setupLocation(event: Event) {
        location_icon.visibleIf(MainActivity.lastLocation != null)
        location.visibleIf(MainActivity.lastLocation != null)

        // Location permissions are revoked/denied
        if (MainActivity.lastLocation != null) {
            val icon = ContextCompat.getDrawable(containerView.context, R.drawable.location)
            icon.setColorFilter(
                    ContextCompat.getColor(containerView.context, R.color.icon_mute),
                    PorterDuff.Mode.SRC_IN
            )
            location_icon.setImageDrawable(icon)

            val distance = event.location.distanceTo(MainActivity.lastLocation)
            if (distance < 1000) {
                location.text = String.format(
                        Locale.getDefault(),
                        containerView.context.string(R.string.location_distance_to_meter),
                        distance
                )
            } else {
                location.text = String.format(
                        Locale.getDefault(),
                        containerView.context.string(R.string.location_distance_to_kilometer),
                        distance / 1000
                )
            }
        }
    }

    private fun setupMeasurementIcons(event: Event) {
        measurement_types.removeAllViews()
        val types = event.measurements.mapTo(HashSet()) { it.type }

        for (type in types) {
            val icon = ImageView(itemView.context)
            icon.setImageResource(type.getResId(itemView.context))
            icon.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            itemView.measurement_types.addView(icon)
        }
    }

}
