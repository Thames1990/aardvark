package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/** Created by thames1990 on 04.12.17. */
enum class MainActivityLayout(
        @StringRes val titleResId: Int,
        @LayoutRes val layoutResId: Int,
        val backgroundColor: () -> Int,
        val iconColor: () -> Int
) {
    TOP_BAR(
            titleResId = R.string.top_bar,
            layoutResId = R.layout.activity_main,
            backgroundColor = { Prefs.headerColor },
            iconColor = { Prefs.iconColor }
    ),

    BOTTOM_BAR(
            titleResId = R.string.bottom_bar,
            layoutResId = R.layout.activity_main_bottom_tabs,
            backgroundColor = { Prefs.backgroundColor },
            iconColor = { Prefs.textColor }
    );

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}