package de.uni_marburg.mathematik.ds.serval.utils

import android.graphics.*
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.toBitmap

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

fun ImageView.flip() {
    val bitmap = drawable.toBitmap()
    val matrix = Matrix().apply { preScale(-1.0f, 1.0f) }
    val flippedBitmap = Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        false
    )
    val canvas = Canvas(flippedBitmap).apply {
        drawBitmap(flippedBitmap, 0.0f, 0.0f, null)
    }
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }
    canvas.drawRect(0.0f, 0.0f, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
    setImageBitmap(flippedBitmap)
}