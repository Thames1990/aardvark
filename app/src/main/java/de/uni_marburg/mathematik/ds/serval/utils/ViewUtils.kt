package de.uni_marburg.mathematik.ds.serval.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import ca.allanwang.kau.utils.snackbar

/**
 * Show themed snackbar with [a text resource][textRes] and a given [builder].
 */
inline fun View.snackbarThemed(
    @StringRes textRes: Int,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(
    textId = textRes,
    duration = Snackbar.LENGTH_LONG,
    builder = snackbarThemed(builder)
)

/**
 * Show themed snackbar with the given [text] and [builder].
 */
inline fun View.snackbarThemed(
    text: String,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(
    text,
    duration = Snackbar.LENGTH_LONG,
    builder = snackbarThemed(builder)
)