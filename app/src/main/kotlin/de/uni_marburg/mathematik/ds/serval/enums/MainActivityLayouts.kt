package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs

/**
 * Defines the layout of the main activity.
 *
 * @property titleRes Resource ID of the title
 * @property layoutRes Resource ID of the layout
 */
enum class MainActivityLayouts(
    @StringRes val titleRes: Int,
    @LayoutRes val layoutRes: Int,
    private val backgroundColorGetter: () -> Int,
    private val iconColorGetter: () -> Int
) {

    TOP_BAR(
        titleRes = R.string.preference_bar_top,
        layoutRes = R.layout.activity_main,
        backgroundColorGetter = AppearancePrefs.Theme::headerColor,
        iconColorGetter = AppearancePrefs.Theme::iconColor
    ),

    BOTTOM_BAR(
        titleRes = R.string.preference_bar_bottom,
        layoutRes = R.layout.activity_main_bottom_tabs,
        backgroundColorGetter = AppearancePrefs.Theme::bgColor,
        iconColorGetter = AppearancePrefs.Theme::textColor
    );

    val bgColor: Int
        get() = backgroundColorGetter()

    val iconColor: Int
        get() = iconColorGetter()

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}