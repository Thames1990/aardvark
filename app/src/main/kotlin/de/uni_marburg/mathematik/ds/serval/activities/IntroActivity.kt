package de.uni_marburg.mathematik.ds.serval.activities

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
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
import de.uni_marburg.mathematik.ds.serval.settings.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInMillis
import de.uni_marburg.mathematik.ds.serval.utils.item
import de.uni_marburg.mathematik.ds.serval.utils.setIconWithOptions
import org.jetbrains.anko.displayMetrics
import org.jetbrains.anko.find

class IntroActivity : BaseActivity() {

    val ripple: RippleCanvas by bindView(R.id.intro_ripple)

    private val indicator: InkPageIndicator by bindView(R.id.intro_indicator)
    private val next: ImageButton by bindView(R.id.intro_next)
    private val skip: Button by bindView(R.id.intro_skip)
    private val viewpager: ViewPager by bindView(R.id.intro_viewpager)

    private val startedFromSettings: Boolean by lazy {
        callingActivity?.className == SettingsActivity::class.java.name
    }
    private val fragments = listOf(
        IntroFragmentWelcome(),
        IntroFragmentTheme(),
        IntroFragmentTabTouch(),
        IntroFragmentEnd()
    )

    private var barHasNext = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        ripple.set(color = AppearancePrefs.Theme.backgroundColor)

        setupViewpager()
        setupBottomBar()
        theme()

        eventViewModel.getFromRepository()
    }

    override fun backConsumer(): Boolean {
        if (viewpager.item > 0) viewpager.item = viewpager.item - 1
        else if (!startedFromSettings) {
            Prefs.lastLaunch = -1L
            finishAffinity()
        } else finish(x = 0F, y = displayMetrics.heightPixels.toFloat())
        return true
    }

    override fun finish() {
        if (!startedFromSettings) {
            Prefs.lastLaunch = currentTimeInMillis
            startActivity<MainActivity>()
        }
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
        fragments.forEach(BaseIntroFragment::themeFragment)
    }

    fun finish(x: Float, y: Float) {
        val flagNotTouchable: Int = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        window.setFlags(flagNotTouchable, flagNotTouchable)

        ripple.ripple(
            color = Themes.AARDVARK_GREEN,
            startX = x,
            startY = y,
            duration = FINISH_ANIMATION_DURATION,
            callback = { postDelayed(delay = 1000, action = ::finish) }
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
                ?.setDuration(FINISH_ANIMATION_DURATION)
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
                    duration = FINISH_ANIMATION_DURATION
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
                duration = FINISH_ANIMATION_DURATION
                start()
            }
        }
    }

    private fun setupViewpager() = with(viewpager) {
        adapter = IntroPageAdapter(fragments)

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

    private fun setupBottomBar() {
        indicator.setViewPager(viewpager)
        with(next) {
            setIcon(
                icon = GoogleMaterial.Icon.gmd_navigate_next,
                color = AppearancePrefs.Theme.iconColor
            )
            setOnClickListener {
                if (barHasNext) viewpager.item = viewpager.currentItem + 1
                else finish(x = next.x + next.pivotX, y = next.y + next.pivotY)
            }
        }
        skip.setOnClickListener { finish() }
    }

    private inner class IntroPageAdapter(
        val fragments: List<BaseIntroFragment>
    ) : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }

    companion object {
        const val FINISH_ANIMATION_DURATION: Long = 600L
    }

}