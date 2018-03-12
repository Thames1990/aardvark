package de.uni_marburg.mathematik.ds.serval.activities

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.ui.widgets.InkPageIndicator
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.intro.*
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.item
import de.uni_marburg.mathematik.ds.serval.utils.setIconWithOptions
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed
import org.jetbrains.anko.find

class IntroActivity : BaseActivity() {

    val ripple: RippleCanvas by bindView(R.id.intro_ripple)

    private val indicator: InkPageIndicator by bindView(R.id.intro_indicator)
    private val next: ImageButton by bindView(R.id.intro_next)
    private val skip: Button by bindView(R.id.intro_skip)
    private val viewpager: ViewPager by bindView(R.id.intro_viewpager)

    private var barHasNext = true

    private val fragments = listOf(
        IntroFragmentWelcome(),
        IntroFragmentTheme(),
        IntroFragmentTabTouch(),
        IntroFragmentEnd()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        with(viewpager) {
            setupViewpager()
            adapter = IntroPageAdapter(supportFragmentManager, fragments)
        }
        indicator.setViewPager(viewpager)
        with(next) {
            setIcon(
                icon = GoogleMaterial.Icon.gmd_navigate_next,
                color = AppearancePrefs.Theme.iconColor
            )
            setOnClickListener {
                when {
                    barHasNext -> viewpager.item = viewpager.currentItem + 1
                    hasLocationPermission -> finish(
                        x = next.x + next.pivotX,
                        y = next.y + next.pivotY
                    )
                    else -> snackbarThemed(R.string.preference_location_requires_location_permission)
                }
            }
        }
        skip.setOnClickListener { finish() }
        ripple.set(color = AppearancePrefs.Theme.backgroundColor)

        theme()
    }

    override fun backConsumer(): Boolean {
        if (viewpager.currentItem > 0) viewpager.item = viewpager.currentItem - 1
        else finishAffinity()
        return true
    }

    override fun finish() {
        startActivity<MainActivity>()
        super.finish()
    }

    fun theme() {
        statusBarColor = AppearancePrefs.Theme.headerColor
        navigationBarColor = AppearancePrefs.Theme.headerColor
        skip.setTextColor(AppearancePrefs.Theme.textColor)
        next.imageTintList = ColorStateList.valueOf(AppearancePrefs.Theme.textColor)
        with(indicator) {
            setColour(AppearancePrefs.Theme.textColor)
            invalidate()
        }
        fragments.forEach { it.themeFragment() }
    }

    fun finish(x: Float, y: Float) {
        val flagNotTouchable: Int = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        window.setFlags(flagNotTouchable, flagNotTouchable)

        ripple.ripple(
            color = Themes.AARDVARK_GREEN,
            startX = x,
            startY = y,
            duration = 600,
            callback = { postDelayed(delay = 1000) { finish() } }
        )

        @Suppress("RemoveExplicitTypeArguments")
        arrayOf(
            skip,
            indicator,
            next,
            fragments.last().view?.find<View>(R.id.intro_title),
            fragments.last().view?.find<View>(R.id.intro_desc)
        ).forEach { view ->
            view?.animate()
                ?.alpha(0f)
                ?.setDuration(600)
                ?.start()
        }

        if (AppearancePrefs.Theme.textColor != Color.WHITE) {
            val image = fragments.last().view?.find<ImageView>(R.id.intro_image)?.drawable
            if (image != null) {
                ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener { animator ->
                        image.setTint(
                            AppearancePrefs.Theme.textColor.blendWith(
                                color = Color.WHITE,
                                ratio = animator.animatedValue as Float
                            )
                        )
                    }
                    duration = 600
                    start()
                }
            }
        }

        if (AppearancePrefs.Theme.headerColor != Themes.AARDVARK_GREEN) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener { animator ->
                    val color = AppearancePrefs.Theme.headerColor.blendWith(
                        color = Themes.AARDVARK_GREEN,
                        ratio = animator.animatedValue as Float
                    )
                    statusBarColor = color
                    navigationBarColor = color
                }
                duration = 600
                start()
            }
        }
    }

    private fun setupViewpager() = with(viewpager) {
        setPageTransformer(true) { page, position ->
            var pageAlpha = 1f
            var pageTranslationX = 0f

            // Only apply to adjacent pages
            if ((position < 0 && position > -1) || (position > 0 && position < 1)) {
                val pageWidth = page.width
                val translateValue = position * -pageWidth

                pageAlpha = if (position < 0) 1 + position else 1f
                pageTranslationX = if (translateValue > -pageWidth) translateValue else 0f
            }

            with(page) {
                alpha = pageAlpha
                translationX = pageTranslationX
            }
        }

        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                fragments[position].onPageScrolled(positionOffset)
                if (position + 1 < fragments.size) {
                    fragments[position + 1].onPageScrolled(positionOffset - 1)
                }
            }

            override fun onPageSelected(position: Int) {
                fragments[position].onPageSelected()
                val hasNext = position != fragments.size - 1
                if (barHasNext == hasNext) return
                barHasNext = hasNext
                next.setIconWithOptions(
                    icon =
                    if (barHasNext) GoogleMaterial.Icon.gmd_navigate_next
                    else GoogleMaterial.Icon.gmd_done,
                    color = AppearancePrefs.Theme.iconColor
                )
                skip.animate().scaleXY(if (barHasNext) 1f else 0f)
            }

        })
    }

    private class IntroPageAdapter(
        fm: FragmentManager,
        val fragments: List<BaseIntroFragment>
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }

}