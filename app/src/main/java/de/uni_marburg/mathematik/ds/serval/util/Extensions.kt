package de.uni_marburg.mathematik.ds.serval.util

import android.view.View
import android.view.ViewTreeObserver

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}
