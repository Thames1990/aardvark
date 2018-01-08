package de.uni_marburg.mathematik.ds.serval.intro

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import kotlin.math.absoluteValue

/** Created by thames1990 on 03.12.17. */
abstract class BaseImageIntroFragment(
        private val titleRes: Int,
        private val imageRes: Int,
        private val descRes: Int
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

    val icon: ImageView by bindViewResettable(R.id.intro_button)

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
        screen.tint(Prefs.backgroundColor)
    }

    fun themeImageComponent(color: Int, vararg ids: Int) {
        ids.forEach { imageDrawable.findDrawableByLayerId(it).tint(color) }
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
}

class IntroAccountFragment : BaseImageIntroFragment(
        R.string.intro_multiple_accounts,
        R.drawable.intro_phone_nav,
        R.string.intro_multiple_accounts_desc
) {

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        themeImageComponent(
                color = Prefs.textColor,
                ids = *intArrayOf(R.id.intro_phone_avatar_1, R.id.intro_phone_avatar_2)
        )
        themeImageComponent(
                color = Prefs.backgroundColor.colorToForeground(),
                ids = *intArrayOf(R.id.intro_phone_nav)
        )
        themeImageComponent(
                color = Prefs.accentColor,
                ids = *intArrayOf(R.id.intro_phone_header)
        )
    }

    override fun onPageScrolledImpl(positionOffset: Float) {
        super.onPageScrolledImpl(positionOffset)
        firstImageFragmentTransition(offset = positionOffset)
    }
}

class IntroTabTouchFragment : BaseImageIntroFragment(
        R.string.intro_easy_navigation,
        R.drawable.intro_phone_tab,
        R.string.intro_easy_navigation_desc
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        icon.visible().setIcon(icon = GoogleMaterial.Icon.gmd_edit, sizeDp = 24)
        icon.setOnClickListener {
            activity?.toast("Editing tabs incoming")
        }
    }

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        themeImageComponent(
                color = Prefs.iconColor,
                ids = *intArrayOf(
                        R.id.intro_phone_icon_1,
                        R.id.intro_phone_icon_2,
                        R.id.intro_phone_icon_3,
                        R.id.intro_phone_icon_4
                )
        )
        themeImageComponent(
                color = Prefs.headerColor,
                ids = *intArrayOf(R.id.intro_phone_tab)
        )
        themeImageComponent(
                color = Prefs.textColor.withAlpha(80),
                ids = *intArrayOf(R.id.intro_phone_icon_ripple)
        )
    }
}

class IntroTabContextFragment : BaseImageIntroFragment(
        R.string.intro_context_aware,
        R.drawable.intro_phone_long_press,
        R.string.intro_context_aware_desc
) {

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        themeImageComponent(
                color = Prefs.headerColor,
                ids = *intArrayOf(R.id.intro_phone_toolbar)
        )
        themeImageComponent(
                color = Prefs.backgroundColor.colorToForeground(0.1f),
                ids = *intArrayOf(R.id.intro_phone_image)
        )
        themeImageComponent(
                color = Prefs.backgroundColor.colorToForeground(0.2f),
                ids = *intArrayOf(R.id.intro_phone_like, R.id.intro_phone_share)
        )
        themeImageComponent(
                color = Prefs.backgroundColor.colorToForeground(0.3f),
                ids = *intArrayOf(R.id.intro_phone_comment)
        )
        themeImageComponent(
                color = Prefs.backgroundColor.colorToForeground(0.1f),
                ids = *intArrayOf(R.id.intro_phone_card_1, R.id.intro_phone_card_2)
        )
        themeImageComponent(
                color = Prefs.textColor,
                ids = *intArrayOf(
                        R.id.intro_phone_image_indicator,
                        R.id.intro_phone_comment_indicator,
                        R.id.intro_phone_card_indicator
                )
        )
    }

    override fun onPageScrolledImpl(positionOffset: Float) {
        super.onPageScrolledImpl(positionOffset)
        lastImageFragmentTransition(offset = positionOffset)
    }
}