package de.uni_marburg.mathematik.ds.serval.intro

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.kotlin.LazyResettableRegistry
import ca.allanwang.kau.utils.*
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.IntroActivity
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import org.jetbrains.anko.childrenSequence
import org.jetbrains.anko.displayMetrics
import kotlin.math.absoluteValue

abstract class BaseIntroFragment(private val layoutRes: Int) : Fragment() {

    private val screenWidth
        get() = resources.displayMetrics.widthPixels

    private val lazyRegistry = LazyResettableRegistry()

    private val viewArray: Array<Array<out View>> by lazyResettableRegistered { viewArray() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutRes, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themeFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Kotterknife.reset(target = this)
        lazyRegistry.invalidateAll()
    }

    private fun translate(offset: Float, views: Array<Array<out View>>) {
        val maxTranslation = offset * screenWidth
        val increment = maxTranslation / views.size

        views.forEachIndexed { index, group ->
            group.forEach { view ->
                view.translationX =
                        if (offset > 0) -maxTranslation + index * increment
                        else -(index + 1) * increment
                view.alpha = 1 - offset.absoluteValue
            }
        }
    }

    // Note that these ids aren't actually inside all layouts.
    // However, they are in most of them, so they are added here for convenience.
    protected val title: TextView by bindViewResettable(R.id.intro_title)
    protected val image: ImageView by bindViewResettable(R.id.intro_image)
    protected val desc: TextView by bindViewResettable(R.id.intro_desc)

    protected fun defaultViewArray(): Array<Array<out View>> = arrayOf(
        arrayOf(title),
        arrayOf(image),
        arrayOf(desc)
    )

    protected open fun themeFragmentImpl() {
        view?.childrenSequence()?.forEach { view ->
            (view as? TextView)?.setTextColor(AppearancePrefs.Theme.textColor)
        }
    }

    protected abstract fun viewArray(): Array<Array<out View>>

    fun <T : Any> lazyResettableRegistered(initializer: () -> T) = lazyRegistry.lazy(initializer)

    fun themeFragment() {
        if (view != null) themeFragmentImpl()
    }

    fun onPageScrolled(positionOffset: Float) {
        if (view != null) onPageScrolledImpl(positionOffset)
    }

    protected open fun onPageScrolledImpl(
        positionOffset: Float
    ) = translate(positionOffset, viewArray)

    fun onPageSelected() {
        if (view != null) onPageSelectedImpl()
    }

    protected open fun onPageSelectedImpl() = Unit

}

class IntroFragmentWelcome : BaseIntroFragment(R.layout.intro_welcome) {

    override fun viewArray(): Array<Array<out View>> = defaultViewArray()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image.setIcon(
            icon = CommunityMaterial.Icon.cmd_gesture_swipe_right,
            sizeDp = requireActivity().displayMetrics.densityDpi,
            color = AppearancePrefs.Theme.textColor
        )
    }

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        image.imageTintList = ColorStateList.valueOf(AppearancePrefs.Theme.textColor)
    }

}

class IntroFragmentEnd : BaseIntroFragment(R.layout.intro_end) {

    override fun viewArray(): Array<Array<out View>> = defaultViewArray()

    private val container: ConstraintLayout by bindViewResettable(R.id.intro_end_container)
    private val description: TextView by bindViewResettable(R.id.intro_desc)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        description.text = view.context.string(R.string.intro_tap_to_exit)
        container.setOnSingleTapListener { _, event ->
            val introActivity: IntroActivity = requireActivity() as IntroActivity
            introActivity.finish(x = event.x, y = event.y)
        }
    }

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        image.imageTintList = ColorStateList.valueOf(AppearancePrefs.Theme.textColor)
    }

}