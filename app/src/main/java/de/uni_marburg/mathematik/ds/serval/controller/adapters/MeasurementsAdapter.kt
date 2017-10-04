package de.uni_marburg.mathematik.ds.serval.controller.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.MeasurementsViewHolder
import de.uni_marburg.mathematik.ds.serval.model.Measurement

/** Adapter for [measurements][Measurement] */
class MeasurementsAdapter(
        val measurements: List<Measurement>,
        private val listener: (Measurement, View) -> Unit
) : RecyclerView.Adapter<MeasurementsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementsViewHolder =
            MeasurementsViewHolder(parent.inflate(R.layout.measurement_row))

    override fun onBindViewHolder(holder: MeasurementsViewHolder, position: Int) =
            holder.bind(measurements[position], listener)

    override fun getItemCount(): Int = measurements.size
}
