package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.MeasurementsViewHolder
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement

/** Adapter for [measurements][Measurement] */
class MeasurementsAdapter(measurements: List<Measurement>) :
        RecyclerView.Adapter<MeasurementsViewHolder>() {

    var measurements = measurements
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.measurement_row, parent, false)
        return MeasurementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeasurementsViewHolder, position: Int) {
        holder.performBind(measurements[position])
    }

    override fun getItemCount(): Int = measurements.size
}
