package de.uni_marburg.mathematik.ds.serval.views

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import de.uni_marburg.mathematik.ds.serval.settings.Experimental

class SwipeToggleViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(motionEvent: MotionEvent?) =
        Experimental.viewpagerSwipeEnabled && super.onInterceptTouchEvent(motionEvent)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean =
        Experimental.viewpagerSwipeEnabled && super.onTouchEvent(motionEvent)

}