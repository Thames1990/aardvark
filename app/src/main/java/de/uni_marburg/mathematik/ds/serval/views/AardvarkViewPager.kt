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

    override fun onInterceptTouchEvent(motionEvent: MotionEvent?) =
        Prefs.viewpagerSwipe && super.onInterceptTouchEvent(motionEvent)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean =
        Prefs.viewpagerSwipe && super.onTouchEvent(motionEvent)

}