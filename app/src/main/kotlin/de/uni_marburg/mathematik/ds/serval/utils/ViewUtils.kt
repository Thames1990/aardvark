package de.uni_marburg.mathematik.ds.serval.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.snackbar

/**
 * Show themed snackbar with [a text resource][textRes] and a given [builder].
 */
fun View.snackbarThemed(
    @StringRes textRes: Int,
    builder: Snackbar.() -> Unit = {}
) = snackbar(
    textId = textRes,
    duration = Snackbar.LENGTH_LONG,
    builder = snackbarThemed(builder)
)

/**
 * Show themed snackbar with the given [text] and [builder].
 */
fun View.snackbarThemed(
    text: String,
    builder: Snackbar.() -> Unit = {}
) = snackbar(
    text,
    duration = Snackbar.LENGTH_LONG,
    builder = snackbarThemed(builder)
)

operator fun ViewGroup.get(position: Int): View = getChildAt(position)