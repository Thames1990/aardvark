package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/**
 * Defines the layout of the main activity.
 *
 * @property titleRes Resource ID of the title
 * @property layoutRes Resource ID of the layout
 */
enum class MainActivityLayout(
    @StringRes val titleRes: Int,
    @LayoutRes val layoutRes: Int,
    private val backgroundColorGetter: () -> Int,
    private val iconColorGetter: () -> Int
) {

    TOP_BAR(
        titleRes = R.string.top_bar,
        layoutRes = R.layout.activity_main,
        backgroundColorGetter = Prefs::headerColor,
        iconColorGetter = Prefs::iconColor
    ),

    BOTTOM_BAR(
        titleRes = R.string.bottom_bar,
        layoutRes = R.layout.activity_main_bottom_tabs,
        backgroundColorGetter = Prefs::backgroundColor,
        iconColorGetter = Prefs::textColor
    );

    val backgroundColor: Int
        get() = backgroundColorGetter()

    val iconColor: Int
        get() = iconColorGetter()

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}