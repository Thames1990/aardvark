package de.uni_marburg.mathematik.ds.serval.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.util.DisplayMetrics

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/** A utiliy class for images. */
object ImageUtil {

    /** Converting [dp] to pixels. */
    fun dpToPixels(context: Context, dp: Int): Int {
        val metrics = context.resources.displayMetrics
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    /** Converts the [id] of a resource to a [BitmapDescriptor]. */
    fun getBitmapDescriptor(context: Context, @DrawableRes id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
        val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
