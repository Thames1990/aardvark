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
import java.util.concurrent.TimeUnit

fun aardvarkAnswers(action: Answers.() -> Unit) {
    if (BuildConfig.DEBUG || !Prefs.analytics) return
    Answers.getInstance().action()
}

fun aardvarkAnswersCustom(name: String, vararg events: Pair<String, Any>) {
    aardvarkAnswers {
        logCustom(CustomEvent("Aardvark $name").apply {
            events.forEach { (key, value) ->
                if (value is Number) putCustomAttribute(key, value)
                else putCustomAttribute(key, value.toString())
            }
        })
    }
}

@SuppressLint("RestrictedApi")
inline fun aardvarkSnackbar(
        crossinline builder: Snackbar.() -> Unit
): Snackbar.() -> Unit = {
    builder()
    // Hacky workaround, but it has proper checks and shouldn't crash
    ((view as? FrameLayout)?.getChildAt(0) as? SnackbarContentLayout)?.apply {
        messageView.setTextColor(Prefs.textColor)
        actionView.setTextColor(Prefs.accentColor)
        //only set if previous text colors are set
        view.setBackgroundColor(Prefs.backgroundColor.withAlpha(255).colorToForeground(0.1f))
    }
}

/**
 * Executes [a function][f].
 *
 * @return True
 */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

/**
 * If the [predicate] is fullfilled, [a function][f] is executed.
 *
 * @return True, if [predicate] is fullfilled; false otherwise.
 */
inline fun consumeIf(predicate: Boolean, f: () -> Unit): Boolean {
    if (predicate) {
        f()
        return true
    }
    return false
}

/** Converts distance in meters **/
fun Float.distanceToString(context: Context): String =
        // in meter
        if (this < 1000) String.format(context.string(R.string.distance_in_meter), this)
        // in kilometer
        else String.format(context.string(R.string.distance_in_kilometer), this.div(1000))

/** Converts UNIX time to human readable information in relation to the current time **/
fun Long.timeToString(context: Context): String {
    val id: Int
    val quantity: Long

    when {
        TimeUnit.MILLISECONDS.toMinutes(this) < 60 -> {
            id = R.plurals.time_minutes_ago
            quantity = TimeUnit.MILLISECONDS.toMinutes(this)
        }
        TimeUnit.MILLISECONDS.toHours(this) < 24   -> {
            id = R.plurals.time_hours_ago
            quantity = TimeUnit.MILLISECONDS.toHours(this)
        }
        TimeUnit.MILLISECONDS.toDays(this) < 30    -> {
            id = R.plurals.time_days_ago
            quantity = TimeUnit.MILLISECONDS.toDays(this)
        }
        TimeUnit.MILLISECONDS.toDays(this) < 365   -> {
            id = R.plurals.time_months_ago
            quantity = TimeUnit.MILLISECONDS.toDays(this) / 30
        }
        else                                       -> {
            id = R.plurals.time_years_ago
            quantity = TimeUnit.MILLISECONDS.toDays(this) / 365
        }
    }

    return context.plural(id, quantity)
}

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