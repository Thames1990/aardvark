package de.uni_marburg.mathematik.ds.serval.model.event

import android.Manifest
import android.graphics.PorterDuff
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.setIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.distanceToString
import de.uni_marburg.mathematik.ds.serval.utils.timeToString
import java.util.*

class EventViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
) {

    private val timeView = itemView.findViewById<TextView>(R.id.time)
    private val measurementsView = itemView.findViewById<LinearLayout>(R.id.measurement_types)
    private val locationIconView = itemView.findViewById<ImageView>(R.id.location_icon)
    private val guideline = itemView.findViewById<Guideline>(R.id.guideline)
    private val locationView = itemView.findViewById<TextView>(R.id.location)

    private var event: Event? = null

    fun bindTo(event: Event?, listener: (Event) -> Unit) {
        this.event = event

        event?.displayTime()
        event?.displayLocation()
        event?.displayMeasurementTypes()

        itemView.setBackgroundColor(Prefs.backgroundColor)
        itemView.setOnClickListener { listener(event!!) }
    }

    private fun Event.displayTime() {
        val timeDifference = Calendar.getInstance().timeInMillis - time
        timeView.apply {
            text = timeDifference.timeToString(itemView.context)
            setTextColor(Prefs.textColor)
        }
    }

    private fun Event.displayLocation() {
        val hasLocationPermission = itemView.context.hasPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (hasLocationPermission) {
            locationIconView.setIcon(
                    icon = GoogleMaterial.Icon.gmd_location_on,
                    color = Prefs.iconColor
            )
            locationView.apply {
                text = location.distanceTo(location).distanceToString(itemView.context)
                setTextColor(Prefs.textColor)
            }
        } else {
            locationIconView.gone()
            locationView.gone()
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guideEnd = 0
            guideline.layoutParams = params
        }
    }

    private fun Event.displayMeasurementTypes() {
        measurementsView.removeAllViews()
        measurements.toHashSet().forEach { measurement ->
            measurementsView.addView(ImageView(itemView.context).apply {
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