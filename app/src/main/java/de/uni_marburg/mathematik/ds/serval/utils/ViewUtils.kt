package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.support.annotation.StringRes
import android.support.design.internal.SnackbarContentLayout
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.FrameLayout
import ca.allanwang.kau.utils.colorToForeground
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.withAlpha

fun View.aardvarkSnackbar(@StringRes textRes: Int, builder: Snackbar.() -> Unit = {})
        = snackbar(textRes, Snackbar.LENGTH_LONG, aardvarkSnackbar(builder))

fun View.aardvarkSnackbar(text: String, builder: Snackbar.() -> Unit = {})
        = snackbar(text, Snackbar.LENGTH_LONG, aardvarkSnackbar(builder))

@SuppressLint("RestrictedApi")
private inline fun aardvarkSnackbar(
        crossinline builder: Snackbar.() -> Unit
): Snackbar.() -> Unit = {
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