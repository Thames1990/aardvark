package de.uni_marburg.mathematik.ds.serval.intro

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import ca.allanwang.kau.utils.colorToForeground
import ca.allanwang.kau.utils.tint
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import kotlin.math.absoluteValue

/** Created by thames1990 on 03.12.17. */
abstract class BaseImageIntroFragment(
        val titleRes: Int,
        val imageRes: Int,
        val descRes: Int
) : BaseIntroFragment(R.layout.intro_image) {

    val imageDrawable: LayerDrawable by lazyResettableRegistered {
        image.drawable as LayerDrawable
    }
    val phone: Drawable by lazyResettableRegistered {
        imageDrawable.findDrawableByLayerId(R.id.intro_phone)
    }
    val screen: Drawable by lazyResettableRegistered {
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
        title.setTextColor(Prefs.textColor)
        desc.setTextColor(Prefs.textColor)
        phone.tint(Prefs.textColor)
        screen.tint(Prefs.bgColor)
    }

    fun themeImageComponent(color: Int, vararg id: Int) {
        id.forEach { imageDrawable.findDrawableByLayerId(it).tint(color) }
    }

    override fun onPageScrolledImpl(positionOffset: Float) {
        super.onPageScrolledImpl(positionOffset)
        val alpha = ((1 - positionOffset.absoluteValue) * 255).toInt()
        // Apply alpha to all layers except phone base
        (0 until imageDrawable.numberOfLayers).forEach {
            val d = imageDrawable.getDrawable(it)
            if (d != phone) d.alpha = alpha
        }
    }

    fun firstImageFragmentTransition(offset: Float) {
        if (offset < 0) image.alpha = 1 - offset
    }

    fun lastImageFragmentTransition(offset: Float) {
        if (offset > 0) image.alpha = 1 - offset
    }

    class IntroAccountFragment : BaseImageIntroFragment(
            R.string.intro_multiple_accounts,
            R.drawable.intro_phone_nav,
            R.string.intro_multiple_accounts_desc
    ) {

        override fun themeFragmentImpl() {
            super.themeFragmentImpl()
            themeImageComponent(
                    Prefs.textColor,
                    R.id.intro_phone_avatar_1,
                    R.id.intro_phone_avatar_2
            )
            themeImageComponent(
                    Prefs.bgColor.colorToForeground(),
                    R.id.intro_phone_nav
            )
            themeImageComponent(Prefs.accentColor, R.id.intro_phone_header)
        }

        override fun onPageScrolledImpl(positionOffset: Float) {
            super.onPageScrolledImpl(positionOffset)
            firstImageFragmentTransition(positionOffset)
        }
    }
}