package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.*
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs

@SuppressLint("NewApi")
inline fun FloatingActionButton.showWithOptions(
    icon: IIcon,
    @StringRes tooltipTextRes: Int,
    @ColorInt color: Int = AppearancePrefs.Theme.iconColor,
    backgroundColor: ColorStateList = AppearancePrefs.Theme.fabColor,
    crossinline onClickListener: () -> Unit,
    show: Boolean = true
) {
    setIcon(icon, color)
    backgroundTintList = backgroundColor
    if (buildIsOreoAndUp) tooltipText = context.string(tooltipTextRes)
    setOnClickListener { onClickListener() }
    showIf(show)
}

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

fun ImageView.setIconWithOptions(
    icon: IIcon?,
    sizeDp: Int = 24,
    @ColorInt color: Int = AppearancePrefs.Theme.iconColor,
    builder: IconicsDrawable.() -> Unit = {},
    animate: Boolean = animationsAreEnabled
) {
    if (animate) fadeScaleTransition { setIcon(icon, sizeDp, color, builder) }
    else setIcon(icon, sizeDp, color, builder)
}

inline fun Snackbar.withAction(
    @StringRes titleRes: Int,
    crossinline onClick: () -> Unit
) = setAction(context.string(titleRes), { onClick() })

fun TextView.setTextWithOptions(
    text: String,
    duration: Long = 200,
    onFinish: (() -> Unit)? = null,
    animate: Boolean = animationsAreEnabled
) = if (animate) setTextWithFade(text, duration, onFinish) else this.text = text

fun TextView.setTextWithOptions(
    textRes: Int,
    duration: Long = 200,
    onFinish: (() -> Unit)? = null,
    animate: Boolean = animationsAreEnabled
) = if (animate) setTextWithFade(textRes, duration, onFinish) else setTextIfValid(textRes)

/**
 * Show themed snackbar with [a text resource][textRes] and a given [builder].
 */
inline fun View.snackbarThemed(
    @StringRes textRes: Int,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(textId = textRes, duration = Snackbar.LENGTH_LONG, builder = snackbarThemed(builder))

/**
 * Show themed snackbar with the given [text] and [builder].
 */
inline fun View.snackbarThemed(
    text: String,
    crossinline builder: Snackbar.() -> Unit = {}
) = snackbar(text, duration = Snackbar.LENGTH_LONG, builder = snackbarThemed(builder))

/**
 * Executes [block] with the View's layoutParams and reassigns the layoutParams with the
 * updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
inline fun View.updateLayoutParams(block: ViewGroup.LayoutParams.() -> Unit) {
    updateLayoutParams<ViewGroup.LayoutParams>(block)
}

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@JvmName("updateLayoutParamsTyped")
inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(block: T.() -> Unit) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}