package de.uni_marburg.mathematik.ds.serval.util

import android.content.Context
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.timeToString(context: Context): String {
    when {
        TimeUnit.MILLISECONDS.toMinutes(this) < 60 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(this)
            )
        TimeUnit.MILLISECONDS.toHours(this) < 24 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.hours_ago),
                    TimeUnit.MILLISECONDS.toHours(this)
            )
        TimeUnit.MILLISECONDS.toDays(this) < 30 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.days_ago),
                    TimeUnit.MILLISECONDS.toDays(this)
            )
        TimeUnit.MILLISECONDS.toDays(this) < 365 ->
            return String.format(
                    Locale.getDefault(),
                    context.getString(R.string.months_ago),
                    TimeUnit.MILLISECONDS.toDays(this).rem(30)
            )
        else -> return String.format(
                Locale.getDefault(),
                context.getString(R.string.years_ago),
                TimeUnit.MILLISECONDS.toDays(this).rem(365)
        )
    }
}

fun Float.distanceToString(context: Context): String = if (this < 1000) {
    String.format(Locale.getDefault(), context.string(R.string.distance_in_meter), this)
} else {
    String.format(Locale.getDefault(), context.string(R.string.distance_in_kilometer), this / 1000)
}

fun ClosedRange<Int>.randomRange() = (start..Random().nextInt(endInclusive - start) + start)

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}