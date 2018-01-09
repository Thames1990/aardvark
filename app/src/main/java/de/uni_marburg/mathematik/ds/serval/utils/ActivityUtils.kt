package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.AnimRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R

fun Activity.aardvarkNavigationBar() {
    navigationBarColor = if (Prefs.tintNavBar) Prefs.headerColor else Color.BLACK
}

fun Activity.aardvarkSnackbar(@StringRes textRes: Int, builder: Snackbar.() -> Unit = {})
        = aardvarkSnackbar(string(textRes), builder)

fun Activity.aardvarkSnackbar(text: String, builder: Snackbar.() -> Unit = {})
        = snackbar(text, Snackbar.LENGTH_LONG, aardvarkSnackbar(builder))

fun Activity.setAardvarkColors(builder: ActivityThemeUtils.() -> Unit) {
    val themer = ActivityThemeUtils()
    themer.builder()
    themer.theme(this)
}

fun Activity.setAardvarkTheme() {
    if (Prefs.backgroundColor.isColorDark) setTheme(R.style.AardvarkTheme)
    else setTheme(R.style.AardvarkTheme_Light)
}

fun Activity.setCurrentScreen() = Aardvark.firebaseAnalytics.setCurrentScreen(
        this,
        javaClass.simpleName,
        null
)

fun Activity.setSecureFlag(secure: Boolean = Prefs.secure_app) {
    val secureFlag: Int = WindowManager.LayoutParams.FLAG_SECURE
    if (secure) window.setFlags(secureFlag, secureFlag)
    else window.clearFlags(secureFlag)
}

fun AppCompatActivity.replaceFragmentSafely(
        fragment: Fragment,
        tag: String,
        allowStateLoss: Boolean = false,
        @IdRes containerViewId: Int,
        @AnimRes enterAnimation: Int = 0,
        @AnimRes exitAnimation: Int = 0,
        @AnimRes popEnterAnimation: Int = 0,
        @AnimRes popExitAnimation: Int = 0
) {
    val ft = supportFragmentManager.beginTransaction()
    ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
    ft.replace(containerViewId, fragment, tag)
    if (!supportFragmentManager.isStateSaved) ft.commit()
    else if (allowStateLoss) ft.commitAllowingStateLoss()
}

fun AppCompatActivity.addFragmentSafely(
        fragment: Fragment,
        tag: String,
        allowStateLoss: Boolean = false,
        @IdRes containerViewId: Int,
        @AnimRes enterAnimation: Int = 0,
        @AnimRes exitAnimation: Int = 0,
        @AnimRes popEnterAnimation: Int = 0,
        @AnimRes popExitAnimation: Int = 0
) {
    val ft = supportFragmentManager.beginTransaction()
    ft.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
    if (!existsFragmentByTag(tag)) ft.add(containerViewId, fragment, tag) else ft.show(fragment)
    if (!supportFragmentManager.isStateSaved) ft.commit()
    else if (allowStateLoss) ft.commitAllowingStateLoss()
}

fun AppCompatActivity.existsFragmentByTag(tag: String): Boolean {
    return supportFragmentManager.findFragmentByTag(tag) != null
}

fun AppCompatActivity.findFragmentByTag(tag: String): Fragment? {
    return supportFragmentManager.findFragmentByTag(tag)
}

class ActivityThemeUtils {

    var themeWindow = true

    private var toolbar: Toolbar? = null
    private var texts = mutableListOf<TextView>()
    private var headers = mutableListOf<View>()
    private var backgrounds = mutableListOf<View>()

    fun toolbar(toolbar: Toolbar) {
        this.toolbar = toolbar
    }

    fun text(vararg views: TextView) {
        texts.addAll(views)
    }

    fun header(vararg views: View) {
        headers.addAll(views)
    }

    fun background(vararg views: View) {
        backgrounds.addAll(views)
    }

    fun theme(activity: Activity) {
        with(activity) {
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
    }

}