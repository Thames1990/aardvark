package de.uni_marburg.mathematik.ds.serval.view.util

import android.content.Context
import android.support.v4.view.ViewPager
import android.view.View
import com.crashlytics.android.Crashlytics
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.view.activities.IntroActivity
import kotlinx.android.synthetic.main.fragment_intro_layout_1.view.*

/**
 * Defines the behaviour and animations of slides in the viewpager of the
 * [intro activity][IntroActivity].
 */
class IntroPageTransformer(private val context: Context) : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val pagePosition = page.tag as Int
        val pageWidth = page.width
        val pageWidthTimesPosition = pageWidth * position
        val absPosition = Math.abs(position)

        if (position <= -1.0f || position >= 1.0f) {
            // The page is not visible
        } else if (position == 0.0f) {
            // The page is selected
        } else {
            // The page is currently being scrolled / swiped

            page.title.alpha = 1.0f - absPosition

            page.description.translationY = -pageWidthTimesPosition / 2f
            page.description.alpha = 1.0f - absPosition

            try {
                page.image.contentDescription = context.resources.getStringArray(
                        R.array.content_description_intro_image
                )[pagePosition]
            } catch (e: ArrayIndexOutOfBoundsException) {
                Crashlytics.logException(e)
            }

            page.image.alpha = 1.0f - absPosition
            page.image.translationX = -pageWidthTimesPosition * 1.5f

            if (position < 0) {
                // Swiping left
            } else {
                // Swiping right
            }
        }
    }

}
