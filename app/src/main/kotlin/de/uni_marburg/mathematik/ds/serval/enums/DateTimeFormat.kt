package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInMillis
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Format for a [ date formatter][DateFormat.format].
 *
 * @property titleRes
 * @property dateStyle
 * @property timeStyle
 */
enum class DateTimeFormat(@StringRes val titleRes: Int, val dateStyle: Int, val timeStyle: Int) {

    FULL_DATE_FULL_TIME(
        titleRes = R.string.date_full_time_full,
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.FULL
    ),

    FULL_DATE_LONG_TIME(
        titleRes = R.string.date_full_time_long,
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.LONG
    ),

    FULL_DATE_MEDIUM_TIME(
        titleRes = R.string.date_full_time_medium,
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.MEDIUM
    ),

    FULL_DATE_SHORT_TIME(
        titleRes = R.string.date_full_time_short,
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.SHORT
    ),

    LONG_DATE_FULL_TIME(
        titleRes = R.string.date_long_time_full,
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.FULL
    ),

    LONG_DATE_LONG_TIME(
        titleRes = R.string.date_long_time_long,
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.LONG
    ),

    LONG_DATE_MEDIUM_TIME(
        titleRes = R.string.date_long_time_medium,
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.MEDIUM
    ),

    LONG_DATE_SHORT_TIME(
        titleRes = R.string.date_long_time_short,
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.FULL
    ),

    MEDIUM_DATE_FULL_TIME(
        titleRes = R.string.date_medium_time_full,
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.FULL
    ),

    MEDIUM_DATE_LONG_TIME(
        titleRes = R.string.date_medium_time_long,
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.LONG
    ),

    MEDIUM_DATE_MEDIUM_TIME(
        titleRes = R.string.date_medium_time_medium,
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.MEDIUM
    ),

    MEDIUM_DATE_SHORT_TIME(
        titleRes = R.string.date_medium_time_short,
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.FULL
    ),

    SHORT_DATE_FULL_TIME(
        titleRes = R.string.date_short_time_full,
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.FULL
    ),

    SHORT_DATE_LONG_TIME(
        titleRes = R.string.date_short_time_long,
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.LONG
    ),

    SHORT_DATE_MEDIUM_TIME(
        titleRes = R.string.date_short_time_medium,
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.MEDIUM
    ),

    SHORT_DATE_SHORT_TIME(
        titleRes = R.string.date_short_time_short,
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.SHORT
    );

    private val format: DateFormat
        get() = SimpleDateFormat.getDateTimeInstance(dateStyle, timeStyle)

    private val dateFormat: DateFormat
        get() = SimpleDateFormat.getDateInstance(dateStyle)

    private val timeFormat: DateFormat
        get() = SimpleDateFormat.getTimeInstance(timeStyle)

    val previewText: String
        get() = formatTime(currentTimeInMillis)

    fun formatTime(time: Long, newLine: Boolean = true): String = if (newLine) {
        val dateString = dateFormat.format(time)
        val timeString = timeFormat.format(time)
        "$dateString\n$timeString"
    } else format.format(time)

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}