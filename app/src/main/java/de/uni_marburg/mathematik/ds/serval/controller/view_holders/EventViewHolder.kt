package de.uni_marburg.mathematik.ds.serval.controller.view_holders

import android.content.Intent
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import kotlinx.android.synthetic.main.event_row.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** [View holder][RecyclerView.ViewHolder] for [events][Event] */
class EventViewHolder(itemview: View) :
        RecyclerView.ViewHolder(itemview),
        View.OnClickListener,
        View.OnLongClickListener {

    lateinit var event: Event

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun performBind(event: Event) {
        this.event = event
        setupTime()
        setupLocation()
        setupMeasurementIcons()
    }

    override fun onClick(view: View) {
        val detail = Intent(itemView.context, DetailActivity::class.java)
        detail.putExtra(DetailActivity.EVENT, event)
        itemView.context.startActivity(detail)
    }

    override fun onLongClick(view: View): Boolean = false

    /** Sets the elapsed time since this [event][event] happened. */
    private fun setupTime() {
        val calendar = Calendar.getInstance()
        val timeDifference = calendar.timeInMillis - event.time
        val format = SimpleDateFormat.getDateInstance(
                DateFormat.MEDIUM,
                Locale.getDefault()
        )
        when {
            TimeUnit.MILLISECONDS.toMinutes(timeDifference) < 60 -> itemView.time.text =
                    String.format(
                            Locale.getDefault(),
                            itemView.context.getString(R.string.time_minutes_ago),
                            TimeUnit.MILLISECONDS.toMinutes(timeDifference)
                    )
            TimeUnit.MILLISECONDS.toHours(timeDifference) < 24 -> itemView.time.text =
                    String.format(
                            Locale.getDefault(),
                            itemView.context.getString(R.string.time_hours_ago),
                            TimeUnit.MILLISECONDS.toHours(timeDifference)
                    )
            TimeUnit.MILLISECONDS.toDays(timeDifference) < 7 -> itemView.time.text =
                    String.format(
                            Locale.getDefault(),
                            itemView.context.getString(R.string.time_days_ago),
                            TimeUnit.MILLISECONDS.toDays(timeDifference)
                    )
            else -> itemView.time.text = format.format(event.time)
        }
    }

    /**
     * Sets the distance from the [last location][MainActivity.lastLocation] to the location of
     * this [event][event].
     */
    private fun setupLocation() {
        // Location permissions are revoked/denied
        if (MainActivity.lastLocation != null) {
            itemView.location_icon.visibility = View.VISIBLE
            itemView.location.visibility = View.VISIBLE

            val icon = ContextCompat.getDrawable(itemView.context, R.drawable.location)
            icon.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.icon_mute),
                    PorterDuff.Mode.SRC_IN
            )
            itemView.location_icon.setImageDrawable(icon)

            val distance = event.location.distanceTo(MainActivity.lastLocation)
            if (distance < 1000) {
                itemView.location.text = String.format(
                        Locale.getDefault(),
                        itemView.context.getString(R.string.location_distance_to_meter),
                        distance
                )
            } else {
                itemView.location.text = String.format(
                        Locale.getDefault(),
                        itemView.context.getString(R.string.location_distance_to_kilometer),
                        distance / 1000
                )
            }
        } else {
            itemView.location_icon.visibility = View.GONE
            itemView.location.visibility = View.GONE
        }
    }

    /**
     * Loads icons for each [measurement][Measurement] available for this
     * [event][EventViewHolder.event].
     */
    private fun setupMeasurementIcons() {
        itemView.measurement_types.removeAllViews()
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
