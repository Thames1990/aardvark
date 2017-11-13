package de.uni_marburg.mathematik.ds.serval.util

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import ca.allanwang.kau.utils.plural
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import java.util.concurrent.TimeUnit

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

/**
 * Sets [secure flag][WindowManager.LayoutParams.FLAG_SECURE], which disables screenshots and
 * beeing able to intercept screen status of the app.
 */
fun Activity.setSecureFlag() {
    if (!BuildConfig.DEBUG) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
}

/** Sets the current screen (activity) for analytics. */
fun Activity.setCurrentScreen() =
        Aardvark.firebaseAnalytics.setCurrentScreen(this, this::class.java.simpleName, null)

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
            quantity = TimeUnit.MILLISECONDS.toDays(this).rem(30)
        }
        else                                       -> {
            id = R.plurals.time_years_ago
            quantity = TimeUnit.MILLISECONDS.toDays(this).rem(365)
        }
    }

    return context.plural(id, quantity)
}

/** Converts distance in meters **/
fun Float.distanceToString(context: Context): String =
        if (this < 1000) String.format(
                context.string(R.string.distance_in_meter),
                this
        ) else String.format(
                context.string(R.string.distance_in_kilometer),
                this.div(1000)
        )