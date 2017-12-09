package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import ca.allanwang.kau.email.EmailBuilder
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.xml.showChangelog
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
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
    if (!BuildConfig.DEBUG && Prefs.useSecureFlag) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

/** Sets the current screen (activity) for analytics. */
fun Activity.setCurrentScreen() = Aardvark.firebaseAnalytics.setCurrentScreen(
        this,
        this::class.java.simpleName,
        null
)

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

/** Converts distance in meters **/
fun Float.distanceToString(context: Context): String =
        // in meter
        if (this < 1000) String.format(context.string(R.string.distance_in_meter), this)
        // in kilometer
        else String.format(context.string(R.string.distance_in_kilometer), this.div(1000))

fun RecyclerView.withDividerDecoration(
        context: Context,
        orientation: Int,
        color: Int = Prefs.colorPrimary
) {
    val divider = DividerItemDecoration(context, orientation)
    val resource = context.drawable(R.drawable.line_divider)
    DrawableCompat.wrap(resource).tint(color)
    divider.setDrawable(resource)
    addItemDecoration(divider)
}

/**
 * Obtain a Context which will store data to device encrypted storage, permitting our app to access
 * it before the user has logged in to the device.
 */
fun Context.safeContext(): Context =
        takeUnless { ContextCompat.isDeviceProtectedStorage(this) }?.run {
            applicationContext.let {
                ContextCompat.createDeviceProtectedStorageContext(it) ?: it
            }
        } ?: this

fun Context.materialDialogThemed(action: MaterialDialog.Builder.() -> Unit): MaterialDialog {
    val builder = MaterialDialog.Builder(this).theme()
    builder.action()
    return builder.show()
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

fun Context.aardvarkChangelog() = showChangelog(R.xml.changelog, Prefs.textColor) {
    theme()
}

inline fun Context.sendAardvarkEmail(
        @StringRes subjectId: Int,
        crossinline builder: EmailBuilder.() -> Unit
) = sendAardvarkEmail(string(subjectId), builder)

inline fun Context.sendAardvarkEmail(
        subjectId: String,
        crossinline builder: EmailBuilder.() -> Unit
) = sendEmail(string(R.string.dev_email), subjectId) {
    builder()
    addItem("Random Aardvark ID", Prefs.aardvarkId)
}

fun Activity.aardvarkNavigationBar() {
    navigationBarColor = if (Prefs.tintNavBar) Prefs.headerColor else Color.BLACK
}

fun Activity.setAardvarkTheme() {
    if (Prefs.backgroundColor.isColorDark) setTheme(R.style.AardvarkTheme)
    else setTheme(R.style.AardvarkTheme_Light)
}

fun Activity.setAardvarkColors(
        toolbar: Toolbar? = null,
        themeWindow: Boolean = true,
        texts: Array<TextView> = arrayOf(),
        headers: Array<View> = arrayOf(),
        backgrounds: Array<View> = arrayOf()
) {
    statusBarColor = Prefs.headerColor.darken(0.1f).withAlpha(255)
    if (Prefs.tintNavBar) navigationBarColor = Prefs.headerColor
    if (themeWindow) window.setBackgroundDrawable(ColorDrawable(Prefs.backgroundColor))
    toolbar?.setBackgroundColor(Prefs.headerColor)
    toolbar?.setTitleTextColor(Prefs.iconColor)
    toolbar?.overflowIcon?.setTint(Prefs.iconColor)
    texts.forEach { it.setTextColor(Prefs.textColor) }
    headers.forEach { it.setBackgroundColor(Prefs.headerColor) }
    backgrounds.forEach { it.setBackgroundColor(Prefs.backgroundColor) }
}

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