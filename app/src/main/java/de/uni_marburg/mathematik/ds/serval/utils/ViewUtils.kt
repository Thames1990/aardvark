package de.uni_marburg.mathematik.ds.serval.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import ca.allanwang.kau.utils.snackbar

fun View.aardvarkSnackbar(@StringRes textRes: Int, builder: Snackbar.() -> Unit = {})
        = snackbar(textRes, Snackbar.LENGTH_LONG, aardvarkSnackbar(builder))

fun View.aardvarkSnackbar(text: String, builder: Snackbar.() -> Unit = {})
        = snackbar(text, Snackbar.LENGTH_LONG, aardvarkSnackbar(builder))