package de.uni_marburg.mathematik.ds.serval.intro

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.ImageView
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.aardvarkSnackbar
import kotlin.math.absoluteValue

/** Created by thames1990 on 03.12.17. */
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
}

class IntroFragmentTabTouch : BaseImageIntroFragment(
    titleRes = R.string.intro_easy_navigation,
    imageRes = R.drawable.intro_phone_tab,
    descRes = R.string.intro_easy_navigation_desc
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        icon.visible().setIcon(icon = GoogleMaterial.Icon.gmd_edit, sizeDp = 24)
        icon.setOnClickListener { it.aardvarkSnackbar(R.string.editing_tabs_coming_soon) }
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