package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/** Created by thames1990 on 04.12.17. */
enum class MainActivityLayout(
    @StringRes val titleRes: Int,
    @LayoutRes val layoutRes: Int,
    private val backgroundColorGetter: () -> Int,
    private val iconColorGetter: () -> Int
) {
    BOTTOM_BAR(
        titleRes = R.string.bottom_bar,
        layoutRes = R.layout.activity_main_bottom_tabs,
        backgroundColorGetter = { Prefs.backgroundColor },
        iconColorGetter = { Prefs.textColor }
    ),
    TOP_BAR(
        titleRes = R.string.top_bar,
        layoutRes = R.layout.activity_main,
        backgroundColorGetter = { Prefs.headerColor },
        iconColorGetter = { Prefs.iconColor }
    );

    val backgroundColor
        get() = backgroundColorGetter()

    val iconColor
        get() = iconColorGetter()

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}