package de.uni_marburg.mathematik.ds.serval.utils

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.design.internal.SnackbarContentLayout
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.BehaviourPrefs
import de.uni_marburg.mathematik.ds.serval.settings.BehaviourPrefs.animationsEnabled
import de.uni_marburg.mathematik.ds.serval.settings.ExperimentalPrefs
import org.jetbrains.anko.bundleOf
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

inline val analyticsEnabled: Boolean
    get() = BehaviourPrefs.analyticsEnabled

inline val animationsEnabled: Boolean
    get() = BehaviourPrefs.animationsEnabled

inline val currentTimeInSeconds: Long
    @SuppressLint("NewApi")
    get() =
        if (buildIsOreoAndUp) Instant.now().epochSecond
        else Calendar.getInstance()[Calendar.SECOND].toLong()

inline val currentTimeInMillis: Long
    @SuppressLint("NewApi")
    get() =
        if (buildIsOreoAndUp) Instant.now().toEpochMilli()
        else Calendar.getInstance().timeInMillis

inline val experimentalSettingsAreEnabled: Boolean
    get() = ExperimentalPrefs.enabled

inline var ViewPager.item
    get() = currentItem
    set(value) = setCurrentItem(value, animationsEnabled)

operator fun ViewGroup.get(position: Int): View = getChildAt(position)

/**
 * Create Fabric Answers instance.
 */
inline fun answers(action: Answers.() -> Unit) = Answers.getInstance().action()

fun doOnDebugBuild(block: () -> Unit) {
    if (isDebugBuild) block()
}

/**
 * Log custom events to analytics services.
 */
fun logAnalytics(name: String, vararg events: Pair<String, Any>) {
    if (analyticsEnabled) {
        answers {
            logCustom(CustomEvent(name).apply {
                events.forEach { (key: String, value: Any) ->
                    if (value is Number) putCustomAttribute(key, value)
                    else putCustomAttribute(key, value.toString())
                }
            })
        }

        events.forEach { (key: String, value: Any) ->
            Aardvark.firebaseAnalytics.logEvent(name, bundleOf(key to value))
        }
    }
}

/**
 * Create themed snackbar.
 */
@SuppressLint("RestrictedApi")
inline fun snackbarThemed(crossinline builder: Snackbar.() -> Unit): Snackbar.() -> Unit = {
    builder()
    val snackbarBaseLayout = view as FrameLayout
    val snackbarContentLayout = snackbarBaseLayout[0] as SnackbarContentLayout
    snackbarContentLayout.apply {
        messageView.setTextColor(AppearancePrefs.Theme.textColor)
        actionView.setTextColor(AppearancePrefs.Theme.accentColor)
        //only set if previous text colors are set
        view.setBackgroundColor(
            AppearancePrefs.Theme.backgroundColor.withAlpha(255).colorToForeground(0.1f)
        )
    }
}

fun AppBarLayout.expand(animate: Boolean = animationsEnabled) = setExpanded(true, animate)

/**
 * Converts distance in meters in formatted string with meters/kilometers.
 */
fun Float.formatDistance(context: Context): String =
    if (this < 1000) String.format(context.string(R.string.distance_in_meter), this)
    else String.format(context.string(R.string.distance_in_kilometer), this.div(1000))

inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
    ViewModelProviders.of(requireActivity())[T::class.java]

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(
    liveData: L,
    body: (T?) -> Unit
) = liveData.observe(this, Observer(body))

/**
 * Converts UNIX time to human readable information in relation to the current time.
 */
fun Long.formatPassedSeconds(context: Context): String {
    val id: Int
    val quantity: Long

    when {
        this < 60 -> {
            id = R.plurals.kau_x_seconds
            quantity = this
        }
        TimeUnit.SECONDS.toMinutes(this) < 60 -> {
            id = R.plurals.kau_x_minutes
            quantity = TimeUnit.SECONDS.toMinutes(this)
        }
        TimeUnit.SECONDS.toHours(this) < 24 -> {
            id = R.plurals.kau_x_hours
            quantity = TimeUnit.SECONDS.toHours(this)
        }
        else -> {
            id = R.plurals.kau_x_days
            quantity = TimeUnit.SECONDS.toDays(this)
        }
    }

    return context.plural(id, quantity)
}

/**
 * Theme material dialog.
 */
fun MaterialDialog.Builder.theme(): MaterialDialog.Builder {
    val dimmerTextColor = AppearancePrefs.Theme.textColor.adjustAlpha(0.8f)
    titleColor(AppearancePrefs.Theme.textColor)
    contentColor(dimmerTextColor)
    widgetColor(dimmerTextColor)
    backgroundColor(AppearancePrefs.Theme.backgroundColor.lighten(0.1f).withMinAlpha(200))
    positiveColor(AppearancePrefs.Theme.textColor)
    negativeColor(AppearancePrefs.Theme.textColor)
    neutralColor(AppearancePrefs.Theme.textColor)
    return this
}