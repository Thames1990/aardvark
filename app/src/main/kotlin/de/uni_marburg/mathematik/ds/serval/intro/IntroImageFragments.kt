package de.uni_marburg.mathematik.ds.serval.intro

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.withAlpha
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayouts
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.flip
import de.uni_marburg.mathematik.ds.serval.utils.setTextWithOptions
import kotlin.math.absoluteValue

abstract class BaseImageIntroFragment(
    @StringRes private val titleRes: Int,
    @DrawableRes private val imageRes: Int,
    @StringRes private val descRes: Int
) : BaseIntroFragment(R.layout.intro_image) {

    private val imageDrawable: LayerDrawable by lazyResettableRegistered {
        image.drawable as LayerDrawable
    }
    private val phone: Drawable by lazyResettableRegistered {
        imageDrawable.findDrawableByLayerId(R.id.intro_phone)
    }
    private val screen: Drawable by lazyResettableRegistered {
        imageDrawable.findDrawableByLayerId(R.id.intro_phone_screen)
    }

    override fun viewArray(): Array<Array<out View>> = arrayOf(arrayOf(title), arrayOf(desc))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        title.setText(titleRes)
        image.setImageResource(imageRes)
        desc.setText(descRes)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        title.setTextColor(AppearancePrefs.Theme.textColor)
        desc.setTextColor(AppearancePrefs.Theme.textColor)
        phone.tint(AppearancePrefs.Theme.textColor)
        screen.tint(AppearancePrefs.Theme.backgroundColor)
    }

    override fun onPageScrolledImpl(positionOffset: Float) {
        super.onPageScrolledImpl(positionOffset)
        val alpha = ((1 - positionOffset.absoluteValue) * 255).toInt()
        // Apply alpha to all layers except phone base
        (0 until imageDrawable.numberOfLayers)
            .map { index -> imageDrawable.getDrawable(index) }
            .filter { drawable -> drawable != phone }
            .forEach { drawable -> drawable.alpha = alpha }
    }

    fun themeImageComponent(color: Int, vararg ids: Int) = ids.forEach { id ->
        imageDrawable.findDrawableByLayerId(id).tint(color)
    }

}

class IntroFragmentTabTouch : BaseImageIntroFragment(
    titleRes = R.string.intro_easy_navigation,
    imageRes = R.drawable.intro_phone_tab,
    descRes = R.string.intro_easy_navigation_desc
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(image) {
            rotation = when (AppearancePrefs.MainActivityLayout.layout) {
                MainActivityLayouts.TOP_BAR -> 0f
                MainActivityLayouts.BOTTOM_BAR -> 180f
            }

            var animationIsRunning = false

            setOnClickListener {
                if (!animationIsRunning) animate()
                    .rotationBy(180f)
                    .setDuration(1000L)
                    .withStartAction { animationIsRunning = true }
                    .withEndAction {
                        animationIsRunning = false
                        flip()
                        AppearancePrefs.MainActivityLayout.index =
                                if (rotation % 360.0f == 0.0f) MainActivityLayouts.TOP_BAR.ordinal
                                else MainActivityLayouts.BOTTOM_BAR.ordinal
                        title.setTextWithOptions(AppearancePrefs.MainActivityLayout.titleRes)
                    }
            }
        }
    }

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        themeImageComponent(
            color = AppearancePrefs.Theme.iconColor,
            ids = *intArrayOf(
                R.id.intro_phone_icon_1,
                R.id.intro_phone_icon_2,
                R.id.intro_phone_icon_3,
                R.id.intro_phone_icon_4
            )
        )
        themeImageComponent(
            color = AppearancePrefs.Theme.headerColor,
            ids = *intArrayOf(R.id.intro_phone_tab)
        )
        themeImageComponent(
            color = AppearancePrefs.Theme.textColor.withAlpha(80),
            ids = *intArrayOf(R.id.intro_phone_icon_ripple)
        )
    }

}