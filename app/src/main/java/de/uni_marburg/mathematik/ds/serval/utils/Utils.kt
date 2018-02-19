package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
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

/**
 * Create Fabric Answers instance.
 */
inline fun aardvarkAnswers(action: Answers.() -> Unit) {
    if (BuildConfig.DEBUG || !Prefs.analytics) return
    Answers.getInstance().action()
}

/**
 * Log custom events to Fabric Answers.
 */
fun aardvarkAnswersCustom(name: String, vararg events: Pair<String, Any>) {
    if (!BuildConfig.DEBUG && Prefs.analytics) aardvarkAnswers {
        logCustom(CustomEvent("Aardvark $name").apply {
            events.forEach { (key, value) ->
                if (value is Number) putCustomAttribute(key, value)
                else putCustomAttribute(key, value.toString())
            }
        })
    }
}

/**
 * Create themed snackbar.
 */
@SuppressLint("RestrictedApi")
inline fun snackbarThemed(crossinline builder: Snackbar.() -> Unit): Snackbar.() -> Unit = {
    builder()
    // Hacky workaround, but it has proper checks and shouldn't crash
    ((view as? FrameLayout)?.getChildAt(0) as? SnackbarContentLayout)?.apply {
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
fun Float.distanceToString(context: Context): String =
    if (this < 1000) String.format(context.string(R.string.distance_in_meter), this)
    else String.format(context.string(R.string.distance_in_kilometer), this.div(1000))

/**
 * Converts UNIX time to human readable information in relation to the current time.
 */
fun Long.timeToString(context: Context): String {
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
    buildIsLollipopAndUp
    return this
}

inline val buildIsOreoAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

inline val currentTimeInSeconds: Long
    @SuppressLint("NewApi")
    get() {
        return if (buildIsOreoAndUp) Instant.now().epochSecond
        else Calendar.getInstance().get(Calendar.SECOND).toLong()
    }