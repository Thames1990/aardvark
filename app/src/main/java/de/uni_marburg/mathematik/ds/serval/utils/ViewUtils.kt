package de.uni_marburg.mathematik.ds.serval.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import ca.allanwang.kau.utils.snackbar

inline fun View.aardvarkSnackbar(
    @StringRes textRes: Int,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(
    textId = textRes,
    duration = Snackbar.LENGTH_LONG,
    builder = aardvarkSnackbar(builder)
)

inline fun View.aardvarkSnackbar(
    text: String,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(
    text,
    duration = Snackbar.LENGTH_LONG,
    builder = aardvarkSnackbar(builder)
)