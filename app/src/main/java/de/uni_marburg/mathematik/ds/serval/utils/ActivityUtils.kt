package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.content.systemService
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Color the navigation bar with the specified [color][Prefs.headerColor], if the user activated
 * [the setting][Prefs.tintNavBar]; [black][Color.BLACK] otherwise.
 */
fun Activity.themeNavigationBar() {
    navigationBarColor = if (Prefs.tintNavBar) Prefs.headerColor else Color.BLACK
}

/**
 * Show themed snackbar with [a text resource][textRes] and a given [builder].
 */
inline fun Activity.snackbarThemed(
    @StringRes textRes: Int,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbarThemed(text = string(textRes), builder = builder)

/**
 * Show themed snackbar with the given [text] and [builder].
 */
inline fun Activity.snackbarThemed(
    text: String,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(text, duration = Snackbar.LENGTH_LONG, builder = snackbarThemed(builder))

/**
 * Restarts an activity from itself with a fade animation.
 *
 * Keeps its existing extra bundles and has a [intent builder][intentBuilder] to accept other
 * parameters.
 */
inline fun Activity.restartActivity(intentBuilder: Intent.() -> Unit = {}) {
    val i = Intent(this, this::class.java)
    val oldExtras = intent.extras
    if (oldExtras != null)
        i.putExtras(oldExtras)
    i.intentBuilder()
    startActivity(i)
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out) //No transitions
    finish()
    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out)
}

/**
 * Force restart the entire application.
 */
@Suppress("NOTHING_TO_INLINE")
@RequiresApi(Build.VERSION_CODES.M)
inline fun Activity.restartApplication() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    val pending = PendingIntent.getActivity(this, 666, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    val alarm = systemService<AlarmManager>()
    if (buildIsMarshmallowAndUp)
        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
    else
        alarm.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, pending)
    finish()
    System.exit(0)
}

/**
 * Set all colors with the given [builder].
 */
inline fun Activity.setAardvarkColors(builder: ActivityThemeUtils.() -> Unit) {
    val themer = ActivityThemeUtils()
    themer.builder()
    themer.theme(this)
}

/**
 * Set a light or dark theme based on the darkness of
 * [the user specified background color][Prefs.backgroundColor].
 */
fun Activity.setAardvarkTheme() =
    if (Prefs.backgroundColor.isColorDark) setTheme(R.style.AardvarkTheme)
    else setTheme(R.style.AardvarkTheme_Light)

/**
 * Sets the treatment of the content of the window as [secure/non-secure][secure], (not) preventing
 * it from appearing in screenshots or from being viewed on non-secure displays.
 */
fun Activity.setSecureFlag(secure: Boolean = Prefs.secure_app) {
    val secureFlag: Int = WindowManager.LayoutParams.FLAG_SECURE
    if (secure) window.setFlags(secureFlag, secureFlag)
    else window.clearFlags(secureFlag)
}

class ActivityThemeUtils {

    /**
     * Determines whether the window background drawable should be coloured
     */
    var themeWindow = true

    private var toolbar: Toolbar? = null
    private var texts = mutableListOf<TextView>()
    private var headers = mutableListOf<View>()
    private var backgrounds = mutableListOf<View>()

    fun toolbar(toolbar: Toolbar) {
        this.toolbar = toolbar
    }

    fun text(vararg views: TextView) = texts.addAll(views)

    fun header(vararg views: View) = headers.addAll(views)

    fun background(vararg views: View) = backgrounds.addAll(views)

    /**
     * Theme the [activity].
     */
    fun theme(activity: Activity) {
        with(activity) {
            statusBarColor = Prefs.headerColor.darken(0.1f).withAlpha(255)
            if (Prefs.tintNavBar) navigationBarColor = Prefs.headerColor
            if (themeWindow) window.setBackgroundDrawable(ColorDrawable(Prefs.backgroundColor))
            toolbar?.apply {
                setBackgroundColor(Prefs.headerColor)
                setTitleTextColor(Prefs.iconColor)
                overflowIcon?.setTint(Prefs.iconColor)
            }
            texts.forEach { textView -> textView.setTextColor(Prefs.textColor) }
            headers.forEach { view -> view.setBackgroundColor(Prefs.headerColor) }
            backgrounds.forEach { view -> view.setBackgroundColor(Prefs.backgroundColor) }
        }
    }

}