package de.uni_marburg.mathematik.ds.serval.intro

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import ca.allanwang.kau.utils.*
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
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

    fun themeImageComponent(color: Int, vararg ids: Int) =
        ids.forEach { id -> imageDrawable.findDrawableByLayerId(id).tint(color) }

    override fun onPageScrolledImpl(positionOffset: Float) {
        super.onPageScrolledImpl(positionOffset)
        val alpha = ((1 - positionOffset.absoluteValue) * 255).toInt()
        // TODO This is only desired, if there are more than one intro image fragments
        // Apply alpha to all layers except phone base
        (0 until imageDrawable.numberOfLayers)
            .map { index -> imageDrawable.getDrawable(index) }
            .filter { drawable -> drawable != phone }
            .forEach { drawable -> drawable.alpha = alpha }
    }
}

class IntroFragmentTabTouch : BaseImageIntroFragment(
    titleRes = R.string.intro_easy_navigation,
    imageRes = R.drawable.intro_phone_tab,
    descRes = R.string.intro_easy_navigation_desc
) {

    private val animationDuration = 1500L

    private var currentRotationDegrees = when (Prefs.mainActivityLayout) {
        MainActivityLayout.TOP_BAR -> 0.0f
        MainActivityLayout.BOTTOM_BAR -> 180.0f
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        icon.apply {
            visible()
            setIcon(
                icon = CommunityMaterial.Icon.cmd_arrow_down,
                sizeDp = 24,
                color = Prefs.textColor
            )
            rotation = currentRotationDegrees
        }
        image.rotation = currentRotationDegrees

        icon.setOnClickListener {
            // TODO Figure out why rotation from 180.0 to 0.0 is wrong
            val rotateAnimation = RotateAnimation(
                currentRotationDegrees,
                currentRotationDegrees - 180.0f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = animationDuration
                fillAfter = true
            }

            val rotateAnimationSet = AnimationSet(true).apply {
                interpolator = DecelerateInterpolator()
                fillAfter = true
                isFillEnabled = true
                addAnimation(rotateAnimation)
            }

            currentRotationDegrees -= 180.0f

            // Flip
            image.setImageBitmap(image.drawable.toBitmap().mirrored())
            // Rotate
            image.startAnimation(rotateAnimationSet)
            icon.fadeScaleTransition(duration = animationDuration) {
                rotation = currentRotationDegrees
            }

            // Set main activity layout type
            Prefs.mainActivityLayoutType =
                    if (currentRotationDegrees.rem(360.0f) == 0.0f)
                        MainActivityLayout.TOP_BAR.ordinal
                    else
                        MainActivityLayout.BOTTOM_BAR.ordinal

            // Update text to indicate current main activity layout type
            title.setTextWithFade(Prefs.mainActivityLayout.titleRes)
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

    /**
     * Mirror bitmap.
     */
    private fun Bitmap.mirrored(): Bitmap {
        val matrix = Matrix().apply {
            preScale(-1.0f, 1.0f)
        }

        val reflection = Bitmap.createBitmap(
            this,
            0,
            0,
            width,
            height,
            matrix,
            false
        )

        val canvas = Canvas(reflection).apply {
            drawBitmap(reflection, 0.0f, 0.0f, null)
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        }

        canvas.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        return reflection
    }
}