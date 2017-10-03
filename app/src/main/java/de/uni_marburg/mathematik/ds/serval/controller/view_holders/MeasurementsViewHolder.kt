package de.uni_marburg.mathematik.ds.serval.controller.view_holders

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.model.MeasurementType
import kotlinx.android.synthetic.main.measurement_row.view.*
import java.util.*


/** [View holder][RecyclerView.ViewHolder] for [measurements][Measurement] */
class MeasurementsViewHolder(itemview: View) :
        RecyclerView.ViewHolder(itemview),
        View.OnClickListener,
        View.OnLongClickListener {

    lateinit var measurement: Measurement

    fun performBind(measurement: Measurement) {
        this.measurement = measurement
        setupViews()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.explore -> TODO()
            R.id.share -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                val text = measurement.type.toString() + " " +
                        itemView.context.getString(R.string.measurement).toLowerCase() +
                        itemView.context.getString(R.string.with_value) + measurement.value
                shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                shareIntent.type = itemView.context.getString(R.string.intent_type_text_plain)
                itemView.context.startActivity(Intent.createChooser(
                        shareIntent,
                        itemView.context.resources.getText(R.string.chooser_title_share_measurement)
                ))
            }
        }
    }

    override fun onLongClick(view: View): Boolean = false

    /** Sets up all views. */
    private fun setupViews() {
        val value: String
        val resId: Int
        when (measurement.type) {
            MeasurementType.PRECIPITATION -> {
                value = itemView.context.getString(R.string.measurement_value_precipitation)
                resId = R.drawable.precipitation
            }
            MeasurementType.RADIATION -> {
                value = itemView.context.getString(R.string.measurement_value_radiation)
                resId = R.drawable.radiation
            }
            MeasurementType.TEMPERATURE -> {
                value = itemView.context.getString(R.string.measurement_value_temperature)
                resId = R.drawable.temperature
            }
            MeasurementType.WIND -> {
                value = itemView.context.getString(R.string.measurement_value_wind)
                resId = R.drawable.wind
            }
        }

        itemView.measurement_type.text = measurement.type.toString()
        itemView.measurement_value.text = String.format(
                Locale.getDefault(),
                value,
                measurement.value
        )
        itemView.measurement_icon.setImageResource(resId)
    }
}
