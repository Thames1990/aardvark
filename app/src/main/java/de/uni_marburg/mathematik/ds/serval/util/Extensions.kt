package de.uni_marburg.mathematik.ds.serval.util

import android.content.Context
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.timeToString(context: Context): String {
    val format = SimpleDateFormat.getDateInstance(
            DateFormat.MEDIUM,
            Locale.getDefault()
    )
    when {
        TimeUnit.MILLISECONDS.toMinutes(this) < 60 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(this)
            )
        TimeUnit.MILLISECONDS.toHours(this) < 24 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_hours_ago),
                    TimeUnit.MILLISECONDS.toHours(this)
            )
        TimeUnit.MILLISECONDS.toDays(this) < 7 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_days_ago),
                    TimeUnit.MILLISECONDS.toDays(this)
            )
        else -> return format.format(this)
    }
}

fun Float.distanceToString(context: Context): String = if (this < 1000) {
    String.format(
            Locale.getDefault(),
            context.string(R.string.location_distance_to_meter),
            this
    )
} else {
    String.format(
            Locale.getDefault(),
            context.string(R.string.location_distance_to_kilometer),
            this / 1000
    )
}