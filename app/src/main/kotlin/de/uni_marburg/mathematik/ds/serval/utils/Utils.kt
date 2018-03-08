package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.internal.SnackbarContentLayout
import android.support.design.widget.Snackbar
import android.widget.FrameLayout
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

inline val analyticsEnabled: Boolean
    get() = !BuildConfig.DEBUG && Prefs.analytics

/**
 * Create Fabric Answers instance.
 */
inline fun answers(action: Answers.() -> Unit) = Answers.getInstance().action()

/**
 * Log custom events to Fabric Answers.
 */
fun answersCustom(name: String, vararg events: Pair<String, Any>) {
    if (analyticsEnabled) {
        answers {
            logCustom(CustomEvent(name).apply {
                events.forEach { (key: String, value: Any) ->
                    if (value is Number) putCustomAttribute(key, value)
                    else putCustomAttribute(key, value.toString())
                }
            })
        }
    }
}

/**
 * Create themed snackbar.
 */
@SuppressLint("RestrictedApi")
inline fun snackbarThemed(crossinline builder: Snackbar.() -> Unit): Snackbar.() -> Unit = {
    builder()
    val snackbarBaseLayout = view as FrameLayout
    val snackbarContentLayout = snackbarBaseLayout[0] as SnackbarContentLayout
    snackbarContentLayout.apply {
        messageView.setTextColor(Prefs.textColor)
        actionView.setTextColor(Prefs.accentColor)
        //only set if previous text colors are set
        view.setBackgroundColor(
            Prefs.backgroundColor.withAlpha(255).colorToForeground(0.1f)
        )
    }
}

/**
 * Converts distance in meters in formatted string with meters/kilometers.
 */
fun Float.formatDistance(context: Context): String =
    if (this < 1000) String.format(context.string(R.string.distance_in_meter), this)
    else String.format(context.string(R.string.distance_in_kilometer), this.div(1000))

/**
 * Converts UNIX time to human readable information in relation to the current time.
 */
fun Long.formatPassedSeconds(context: Context): String {
    val id: Int
    val quantity: Long

    when {
        this < 60 -> {
            id = R.plurals.kau_x_seconds
            quantity = this
        }
        TimeUnit.SECONDS.toMinutes(this) < 60 -> {
            id = R.plurals.kau_x_minutes
            quantity = TimeUnit.SECONDS.toMinutes(this)
        }
        TimeUnit.SECONDS.toHours(this) < 24 -> {
            id = R.plurals.kau_x_hours
            quantity = TimeUnit.SECONDS.toHours(this)
        }
        else -> {
            id = R.plurals.kau_x_days
            quantity = TimeUnit.SECONDS.toDays(this)
        }
    }

    return context.plural(id, quantity)
}

/**
 * Theme material dialog.
 */
fun MaterialDialog.Builder.theme(): MaterialDialog.Builder {
    val dimmerTextColor = Prefs.textColor.adjustAlpha(0.8f)
    titleColor(Prefs.textColor)
    contentColor(dimmerTextColor)
    widgetColor(dimmerTextColor)
    backgroundColor(Prefs.backgroundColor.lighten(0.1f).withMinAlpha(200))
    positiveColor(Prefs.textColor)
    negativeColor(Prefs.textColor)
    neutralColor(Prefs.textColor)
    return this
}

inline val currentTimeInSeconds: Long
    @SuppressLint("NewApi")
    get() {
        return if (buildIsOreoAndUp) Instant.now().epochSecond
        else Calendar.getInstance().get(Calendar.SECOND).toLong()
    }

inline val currentTimeInMillis: Long
    @SuppressLint("NewApi")
    get() {
        return if (buildIsOreoAndUp) Instant.now().toEpochMilli()
        else Calendar.getInstance().timeInMillis
    }