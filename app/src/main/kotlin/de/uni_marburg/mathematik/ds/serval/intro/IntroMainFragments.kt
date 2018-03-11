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
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_WRITE_EXTERNAL_STORAGE
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.IntroActivity
import de.uni_marburg.mathematik.ds.serval.utils.*
import org.jetbrains.anko.childrenSequence
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

    protected open fun themeFragmentImpl() = view?.childrenSequence()?.forEach { view ->
        (view as? TextView)?.setTextColor(Prefs.Appearance.textColor)
    }

    protected abstract fun viewArray(): Array<Array<out View>>

    fun <T : Any> lazyResettableRegistered(initializer: () -> T) = lazyRegistry.lazy(initializer)

    fun themeFragment() {
        if (view != null) themeFragmentImpl()
    }

    fun onPageScrolled(positionOffset: Float) {
        if (view != null) onPageScrolledImpl(positionOffset)
    }

    protected open fun onPageScrolledImpl(positionOffset: Float) =
        translate(positionOffset, viewArray)

    fun onPageSelected() {
        if (view != null) onPageSelectedImpl()
    }

    protected open fun onPageSelectedImpl() = Unit

}

class IntroFragmentWelcome : BaseIntroFragment(R.layout.intro_welcome) {

    override fun viewArray(): Array<Array<out View>> = defaultViewArray()

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        image.imageTintList = ColorStateList.valueOf(Prefs.Appearance.textColor)
    }
}

class IntroFragmentEnd : BaseIntroFragment(R.layout.intro_end) {

    private val container: ConstraintLayout by bindViewResettable(R.id.intro_end_container)
    private val description: TextView by bindView(R.id.intro_desc)

    override fun viewArray(): Array<Array<out View>> = defaultViewArray()

    override fun themeFragmentImpl() {
        super.themeFragmentImpl()
        image.imageTintList = ColorStateList.valueOf(Prefs.Appearance.textColor)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(view.context) {
            description.text =
                    if (hasLocationPermission) string(R.string.intro_tap_to_exit)
                    else string(R.string.grant_location_permission)
        }

        container.setOnSingleTapListener { _, motionEvent ->
            with(requireActivity()) {
                if (isDebugBuild) {
                    if (hasLocationPermission && hasWriteExternalStoragePermission) {
                        val introActivity = this as IntroActivity
                        introActivity.finish(x = motionEvent.x, y = motionEvent.y)
                    } else {
                        kauRequestPermissions(
                            permissions = *arrayOf(
                                PERMISSION_ACCESS_FINE_LOCATION,
                                PERMISSION_WRITE_EXTERNAL_STORAGE
                            ),
                            callback = { granted, deniedPerm ->
                                if (!granted) deniedPerm?.let { snackbarThemed(it) }
                                else description.setTextWithOptions(R.string.intro_tap_to_exit)
                            }
                        )
                    }
                } else {
                    if (hasLocationPermission) {
                        val introActivity = this as IntroActivity
                        introActivity.finish(x = motionEvent.x, y = motionEvent.y)
                    } else {
                        kauRequestPermissions(
                            permissions = *arrayOf(PERMISSION_ACCESS_FINE_LOCATION),
                            callback = { granted, deniedPerm ->
                                if (!granted) deniedPerm?.let { snackbarThemed(it) }
                                else description.setTextWithOptions(R.string.intro_tap_to_exit)
                            }
                        )
                    }
                }
            }
        }
    }
}