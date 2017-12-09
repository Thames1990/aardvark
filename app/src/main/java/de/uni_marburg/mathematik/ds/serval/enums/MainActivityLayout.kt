package de.uni_marburg.mathematik.ds.serval.enums

import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/** Created by thames1990 on 04.12.17. */
enum class MainActivityLayout(
        val titleRes: Int,
        val layoutRes: Int,
        val backgroundColor: () -> Int,
        val iconColor: () -> Int
) {
    TOP_BAR(
            R.string.top_bar,
            R.layout.activity_main,
            { Prefs.headerColor },
            { Prefs.iconColor }
    ),

    BOTTOM_BAR(
            R.string.bottom_bar,
            R.layout.activity_main_bottom_tabs,
            { Prefs.backgroundColor },
            { Prefs.textColor }
    );

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}