package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.settings.Appearance
import de.uni_marburg.mathematik.ds.serval.settings.Experimental

/**
 * Color the navigation bar with the specified [color][Appearance.Theme.headerColor],
 * if the user activated [the setting][Appearance.tintNavBar]; [black][Color.BLACK] otherwise.
 */
fun Activity.themeNavigationBar() {
    navigationBarColor =
            if (Appearance.tintNavBar) Appearance.Theme.headerColor
            else Color.BLACK
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
 * Set all colors with the given [builder].
 */
inline fun Activity.setColors(builder: ActivityThemeUtils.() -> Unit) {
    val themer = ActivityThemeUtils()
    themer.builder()
    themer.theme(this)
}

/**
 * Set a light or dark theme based on the darkness of
 * [the user specified background color][Appearance.Theme.backgroundColor].
 */
fun Activity.setTheme() =
    if (Appearance.Theme.backgroundColor.isColorDark) setTheme(R.style.AardvarkTheme)
    else setTheme(R.style.AardvarkTheme_Light)

/**
 * Sets the treatment of the content of the window as [secure/non-secure][secure], (not) preventing
 * it from appearing in screenshots or from being viewed on non-secure displays.
 */
fun Activity.setSecureFlag(secure: Boolean = Experimental.secureApp) {
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
            statusBarColor = Appearance.Theme.headerColor.darken(0.1f).withAlpha(255)
            if (Appearance.tintNavBar) navigationBarColor = Appearance.Theme.headerColor
            if (themeWindow) window.setBackgroundDrawable(ColorDrawable(Appearance.Theme.backgroundColor))
            toolbar?.apply {
                setBackgroundColor(Appearance.Theme.headerColor)
                setTitleTextColor(Appearance.Theme.iconColor)
                overflowIcon?.setTint(Appearance.Theme.iconColor)
                popupTheme = when (Appearance.Theme.theme) {
                    Themes.LIGHT -> R.style.AppTheme_PopupOverlay
                    Themes.DARK -> R.style.AppTheme_PopupOverlay_Dark
                    Themes.AMOLED -> R.style.AppTheme_PopupOverlay_Dark
                    Themes.CUSTOM -> R.style.AppTheme_PopupOverlay // TODO Set theme accordingly
                }
            }
            texts.forEach { textView -> textView.setTextColor(Appearance.Theme.textColor) }
            headers.forEach { view -> view.setBackgroundColor(Appearance.Theme.headerColor) }
            backgrounds.forEach { view -> view.setBackgroundColor(Appearance.Theme.backgroundColor) }
        }
    }

}