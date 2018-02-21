package de.uni_marburg.mathematik.ds.serval.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

class AardvarkViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    var enableSwipe = true

    override fun onInterceptTouchEvent(motionEvent: MotionEvent?) =
        try {
            Prefs.viewpagerSwipe && enableSwipe && super.onInterceptTouchEvent(motionEvent)
        } catch (e: IllegalArgumentException) {
            false
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean =
        try {
            Prefs.viewpagerSwipe && enableSwipe && super.onTouchEvent(motionEvent)
        } catch (e: IllegalArgumentException) {
            false
        }
}