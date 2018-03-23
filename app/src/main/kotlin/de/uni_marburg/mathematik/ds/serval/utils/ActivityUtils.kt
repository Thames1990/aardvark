package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs

/**
 * Initialize the contents of the Activity's standard options menu.
 * You should place your menu items in to [menu].
 *
 * @param menuRes
 * @param menu
 * @param color
 * @param iicons
 */
fun Activity.createOptionsMenu(
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorInt color: Int = AppearancePrefs.Theme.iconColor,
    vararg iicons: Pair<Int, IIcon>
): Boolean {
    menu ?: return false
    menuInflater.inflate(menuRes, menu)
    setMenuIcons(menu, color, *iicons)
    return true
}

/**
 * Set all colors with the given [builder].
 */
inline fun Activity.setColors(builder: ActivityThemeUtils.() -> Unit) {
    val themer = ActivityThemeUtils()
    themer.builder()
    themer.theme(this)
}

/**
 * Sets the treatment of the content of the window as [secure/non-secure][secure], (not) preventing
 * it from appearing in screenshots or from being viewed on non-secure displays.
 */
fun Activity.setSecureFlag(secure: Boolean = appIsSecured) {
    val secureFlag: Int = WindowManager.LayoutParams.FLAG_SECURE
    if (secure) window.setFlags(secureFlag, secureFlag) else window.clearFlags(secureFlag)
}

/**
 * Set a light or dark theme based on the darkness of
 * [the user specified background color][AppearancePrefs.Theme.backgroundColor].
 */
fun Activity.setTheme() = setTheme(
    if (AppearancePrefs.Theme.backgroundColor.isColorDark) R.style.AardvarkTheme
    else R.style.AardvarkTheme_Light
)

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
 * Color the navigation bar with the specified [color][AppearancePrefs.Theme.headerColor],
 * if the user activated [the setting][AppearancePrefs.tintNavBar]; [black][Color.BLACK] otherwise.
 */
fun Activity.themeNavigationBar() {
    navigationBarColor =
            if (AppearancePrefs.tintNavBar) AppearancePrefs.Theme.headerColor
            else Color.BLACK
}

/**
 * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
 * is alive. More detailed explanation is in {@link ViewModel}.
 * <p>
 * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
 *
 * @return a ViewModelProvider instance
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T =
    ViewModelProviders.of(this)[T::class.java]

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
    fun theme(activity: Activity) = with(activity) {
        statusBarColor = AppearancePrefs.Theme.headerColor.darken(0.1f).withAlpha(255)
        if (AppearancePrefs.tintNavBar) navigationBarColor = AppearancePrefs.Theme.headerColor
        if (themeWindow) window.setBackgroundDrawable(ColorDrawable(AppearancePrefs.Theme.backgroundColor))
        toolbar?.apply {
            setBackgroundColor(AppearancePrefs.Theme.headerColor)
            setTitleTextColor(AppearancePrefs.Theme.iconColor)
            overflowIcon?.setTint(AppearancePrefs.Theme.iconColor)
            popupTheme = when (AppearancePrefs.Theme.theme) {
                Themes.LIGHT -> R.style.AppTheme_PopupOverlay
                Themes.DARK -> R.style.AppTheme_PopupOverlay_Dark
                Themes.AMOLED -> R.style.AppTheme_PopupOverlay_Dark
                Themes.CUSTOM -> R.style.AppTheme_PopupOverlay // TODO Set theme accordingly
            }
        }
        texts.forEach { it.setTextColor(AppearancePrefs.Theme.textColor) }
        headers.forEach { it.setBackgroundColor(AppearancePrefs.Theme.headerColor) }
        backgrounds.forEach { it.setBackgroundColor(AppearancePrefs.Theme.backgroundColor) }
    }

}