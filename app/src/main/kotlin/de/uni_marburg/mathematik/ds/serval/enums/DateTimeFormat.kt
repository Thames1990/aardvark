package de.uni_marburg.mathematik.ds.serval.enums

import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInSeconds
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

enum class DateTimeFormat(val dateStyle: Int, val timeStyle: Int) {

    FULL_DATE_FULL_TIME(
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.FULL
    ),

    FULL_DATE_LONG_TIME(
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.LONG
    ),

    FULL_DATE_MEDIUM_TIME(
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.MEDIUM
    ),

    FULL_DATE_SHORT_TIME(
        dateStyle = DateFormat.FULL,
        timeStyle = DateFormat.SHORT
    ),

    LONG_DATE_FULL_TIME(
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.FULL
    ),

    LONG_DATE_LONG_TIME(
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.LONG
    ),

    LONG_DATE_MEDIUM_TIME(
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.MEDIUM
    ),

    LONG_DATE_SHORT_TIME(
        dateStyle = DateFormat.LONG,
        timeStyle = DateFormat.FULL
    ),

    MEDIUM_DATE_FULL_TIME(
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.FULL
    ),

    MEDIUM_DATE_LONG_TIME(
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.LONG
    ),

    MEDIUM_DATE_MEDIUM_TIME(
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.MEDIUM
    ),

    MEDIUM_DATE_SHORT_TIME(
        dateStyle = DateFormat.MEDIUM,
        timeStyle = DateFormat.FULL
    ),

    SHORT_DATE_FULL_TIME(
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.FULL
    ),

    SHORT_DATE_LONG_TIME(
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.LONG
    ),

    SHORT_DATE_MEDIUM_TIME(
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.MEDIUM
    ),

    SHORT_DATE_SHORT_TIME(
        dateStyle = DateFormat.SHORT,
        timeStyle = DateFormat.SHORT
    );

    private val format: DateFormat
        get() = SimpleDateFormat.getDateTimeInstance(dateStyle, timeStyle)

    val previewText: String
        get() = format.format(TimeUnit.SECONDS.toMillis(currentTimeInSeconds))

    fun formatTime(time: Long): String = format.format(TimeUnit.SECONDS.toMillis(time))

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}