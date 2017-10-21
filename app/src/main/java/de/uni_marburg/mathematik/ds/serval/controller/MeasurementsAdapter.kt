package de.uni_marburg.mathematik.ds.serval.controller

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement
import de.uni_marburg.mathematik.ds.serval.model.event.MeasurementType.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.measurement_row.*
import java.util.*

class MeasurementsAdapter(
        val measurements: List<Measurement>,
        private val listener: (Measurement, View) -> Unit
) : RecyclerView.Adapter<MeasurementsAdapter.MeasurementsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsViewHolder =
            MeasurementsViewHolder(parent.inflate(R.layout.measurement_row))

    override fun onBindViewHolder(holder: MeasurementsViewHolder, position: Int) =
            holder.bindTo(measurements[position], listener)

    override fun getItemCount(): Int = measurements.size

    class MeasurementsViewHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bindTo(measurement: Measurement, listener: (Measurement, View) -> Unit) {
            display(measurement)
            explore.setOnClickListener { listener(measurement, explore) }
            share.setOnClickListener { listener(measurement, share) }
        }

        private fun display(measurement: Measurement) {
            val value = with(containerView.context) {
                measurement_type.text = string(measurement.type.res)
                when (measurement.type) {
                    PRECIPITATION -> string(R.string.measurement_value_precipitation)
                    RADIATION -> string(R.string.measurement_value_radiation)
                    TEMPERATURE -> string(R.string.measurement_value_temperature)
                    WIND -> string(R.string.measurement_value_wind)
                }
            }
            measurement_value.text = String.format(Locale.getDefault(), value, measurement.value)
            measurement_icon.setImageResource(measurement.type.resId)
        }
    }
}
