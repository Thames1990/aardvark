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
import de.uni_marburg.mathematik.ds.serval.intro.*
import de.uni_marburg.mathematik.ds.serval.intro.BaseIntroFragment.IntroFragmentEnd
import de.uni_marburg.mathematik.ds.serval.intro.BaseIntroFragment.IntroFragmentWelcome
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import org.jetbrains.anko.find

class IntroActivity : BaseActivity() {

    val ripple: RippleCanvas by bindView(R.id.intro_ripple)

    private val indicator: InkPageIndicator by bindView(R.id.intro_indicator)
    private val next: ImageButton by bindView(R.id.intro_next)
    private val skip: Button by bindView(R.id.intro_skip)
    private val viewpager: ViewPager by bindView(R.id.intro_viewpager)

    private lateinit var adapter: IntroPageAdapter

    private var barHasNext = true

    private val fragments = listOf(
            IntroFragmentWelcome(),
            IntroFragmentTheme(),
            IntroAccountFragment(),
            IntroTabTouchFragment(),
            IntroTabContextFragment(),
            IntroFragmentEnd()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        adapter = IntroPageAdapter(supportFragmentManager, fragments)
        viewpager.init()
        indicator.setViewPager(viewpager)
        next.apply {
            setIcon(GoogleMaterial.Icon.gmd_navigate_next)
            setOnClickListener {
                if (barHasNext) viewpager.setCurrentItem(viewpager.currentItem + 1, true)
                else finish(next.x + next.pivotX, next.y + next.pivotY)
            }
        }
        skip.setOnClickListener { finish() }
        ripple.set(Prefs.backgroundColor)
        theme()
    }

    override fun backConsumer(): Boolean {
        if (viewpager.currentItem > 0) {
            viewpager.setCurrentItem(viewpager.currentItem - 1, true)
            return true
        }
        finishAffinity()
        return true
    }

    override fun finish() {
        Prefs.isFirstLaunch = false
        startActivity(MainActivity::class.java)
        super.finish()
    }

    private fun ViewPager.init() {
        setPageTransformer(true) { page, position ->
            // Only apply to adjacent pages
            if ((position < 0 && position > -1) || (position > 0 && position < 1)) {
                val pageWidth = page.width
                val translateValue = position * -pageWidth
                page.translationX = if (translateValue > -pageWidth) translateValue else 0f
                page.alpha = if (position < 0) 1 + position else 1f
            } else {
                page.alpha = 1f
                page.translationX = 0f
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
                next.fadeScaleTransition {
                    setIcon(
                            if (barHasNext) GoogleMaterial.Icon.gmd_navigate_next
                            else GoogleMaterial.Icon.gmd_done,
                            color = Prefs.textColor
                    )
                }
                skip.animate().scaleXY(if (barHasNext) 1f else 0f)
            }

        })
        adapter = this@IntroActivity.adapter
    }

    fun theme() {
        statusBarColor = Prefs.headerColor
        navigationBarColor = Prefs.headerColor
        skip.setTextColor(Prefs.textColor)
        next.imageTintList = ColorStateList.valueOf(Prefs.textColor)
        indicator.setColour(Prefs.textColor)
        indicator.invalidate()
        fragments.forEach { it.themeFragment() }
    }

    fun finish(x: Float, y: Float) {
        val green = color(R.color.aardvark_green)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        ripple.ripple(green, x, y, 600) {
            postDelayed(1000) { finish() }
        }
        @Suppress("RemoveExplicitTypeArguments")
        arrayOf(
                skip,
                indicator,
                next,
                fragments.last().view?.find<View>(R.id.intro_title),
                fragments.last().view?.find<View>(R.id.intro_desc)
        ).forEach {
            it?.animate()?.alpha(0f)?.setDuration(600)?.start()
        }
        if (Prefs.textColor != Color.WHITE) {
            val f = fragments.last().view?.find<ImageView>(R.id.intro_image)?.drawable
            if (f != null) {
                ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        f.setTint(Prefs.textColor.blendWith(Color.WHITE, it.animatedValue as Float))
                    }
                    duration = 600
                    start()
                }
            }
        }
        if (Prefs.headerColor != green) {
            ValueAnimator.ofFloat(0f, 1f).apply {
                addUpdateListener {
                    val color = Prefs.headerColor.blendWith(green, it.animatedValue as Float)
                    statusBarColor = color
                    navigationBarColor = color
                }
                duration = 600
                start()
            }
        }
    }

    class IntroPageAdapter(
            fm: FragmentManager,
            private val fragments: List<BaseIntroFragment>
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }
}