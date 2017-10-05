package de.uni_marburg.mathematik.ds.serval.controller

import android.support.v7.widget.RecyclerView
import android.view.View
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.model.MeasurementType
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.measurement_row.*
import kotlinx.android.synthetic.main.measurement_row.view.*
import java.util.*


/** [View holder][RecyclerView.ViewHolder] for [measurements][Measurement] */
class MeasurementsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(measurement: Measurement, listener: (Measurement, View) -> Unit) =
            with(containerView) {
                setupViews(measurement)
                explore.setOnClickListener { listener(measurement, explore) }
                share.setOnClickListener { listener(measurement, share) }
            }

    private fun setupViews(measurement: Measurement) {
        val value: String
        val resId: Int
        when (measurement.type) {
            MeasurementType.PRECIPITATION -> {
                value = itemView.context.string(R.string.measurement_value_precipitation)
                resId = R.drawable.precipitation
            }
            MeasurementType.RADIATION -> {
                value = itemView.context.string(R.string.measurement_value_radiation)
                resId = R.drawable.radiation
            }
            MeasurementType.TEMPERATURE -> {
                value = itemView.context.string(R.string.measurement_value_temperature)
                resId = R.drawable.temperature
            }
            MeasurementType.WIND -> {
                value = itemView.context.string(R.string.measurement_value_wind)
                resId = R.drawable.wind
            }
        }

        measurement_type.text = measurement.type.toString()
        measurement_value.text = String.format(
                Locale.getDefault(),
                value,
                measurement.value
        )
        measurement_icon.setImageResource(resId)
    }
}
