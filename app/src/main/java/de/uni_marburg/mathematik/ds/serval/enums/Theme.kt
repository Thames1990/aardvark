package de.uni_marburg.mathematik.ds.serval.enums

import android.graphics.Color
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

const val AARDVARK_GREEN = 0xff4CAF50.toInt()
const val AARDVARK_GREEN_LIGHT = 0xff80E27E.toInt()

enum class Theme(
        @StringRes val textRes: Int,
        private val textColorGetter: () -> Int,
        private val accentColorGetter: () -> Int,
        private val backgroundColorGetter: () -> Int,
        private val headerColorGetter: () -> Int,
        private val iconColorGetter: () -> Int
) {

    LIGHT(
            R.string.kau_light,
            { 0xde000000.toInt() },
            { AARDVARK_GREEN },
            { 0xfffafafa.toInt() },
            { AARDVARK_GREEN },
            { Color.WHITE }
    ),

    DARK(
            R.string.kau_dark,
            { Color.WHITE },
            { AARDVARK_GREEN_LIGHT },
            { 0xff303030.toInt() },
            { 0xff2e4b86.toInt() },
            { Color.WHITE }
    ),

    AMOLED(
            R.string.kau_amoled,
            { Color.WHITE },
            { AARDVARK_GREEN_LIGHT },
            { Color.BLACK },
            { Color.BLACK },
            { Color.WHITE }
    ),

    CUSTOM(
            R.string.kau_custom,
            { Prefs.customTextColor },
            { Prefs.customAccentColor },
            { Prefs.customBackgroundColor },
            { Prefs.customHeaderColor },
            { Prefs.customIconColor }
    );

    val textColor: Int
        get() = textColorGetter()

    val accentColor: Int
        get() = accentColorGetter()

    val bgColor: Int
        get() = backgroundColorGetter()

    val headerColor: Int
        get() = headerColorGetter()

    val iconColor: Int
        get() = iconColorGetter()

    companion object {
        val values = values() //save one instance
        operator fun invoke(index: Int) = values[index]
    }
}