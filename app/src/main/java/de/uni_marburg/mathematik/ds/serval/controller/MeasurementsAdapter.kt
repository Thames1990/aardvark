package de.uni_marburg.mathematik.ds.serval.controller

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.inflate
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement

/**
 * [Adapter][RecyclerView.Adapter] for [measurements][Measurement].
 *
 * @param measurements Events bound by this adapter
 * @param listener [Click listener lambda][View.OnClickListener]
 */
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
