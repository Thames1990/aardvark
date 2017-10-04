package de.uni_marburg.mathematik.ds.serval.view.util

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
class IntroPageTransformer : ViewPager.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        with(page) {
            val pageWidthTimesPosition = width * position
            val absPosition = Math.abs(position)

            if (position <= -1.0f || position >= 1.0f) {
                // The page is not visible
            } else if (position == 0.0f) {
                // The page is selected
            } else {
                // The page is currently being scrolled / swiped
                title.alpha = 1.0f - absPosition

                description.translationY = -pageWidthTimesPosition / 2f
                description.alpha = 1.0f - absPosition

                try {
                    image.contentDescription = context.resources.getStringArray(
                            R.array.content_description_intro_image
                    )[tag as Int]
                } catch (e: ArrayIndexOutOfBoundsException) {
                    Crashlytics.logException(e)
                }

                image.alpha = 1.0f - absPosition
                image.translationX = -pageWidthTimesPosition * 1.5f

                if (position < 0) {
                    // Swiping left
                } else {
                    // Swiping right
                }
            }
        }
    }

}
