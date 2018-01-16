package de.uni_marburg.mathematik.ds.serval.enums

import android.graphics.Color
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

enum class Theme(
        @StringRes val textResId: Int,
        private val textColorGetter: () -> Int,
        private val accentColorGetter: () -> Int,
        private val backgroundColorGetter: () -> Int,
        private val headerColorGetter: () -> Int,
        private val iconColorGetter: () -> Int
) {

    LIGHT(
            textResId = R.string.aardvark_light,
            textColorGetter = { 0xde000000.toInt() },
            accentColorGetter = { AARDVARK_GREEN },
            backgroundColorGetter = { 0xfffafafa.toInt() },
            headerColorGetter = { AARDVARK_GREEN },
            iconColorGetter = { Color.WHITE }
    ),

    DARK(
            textResId = R.string.aardvark_dark,
            textColorGetter = { Color.WHITE },
            accentColorGetter = { AARDVARK_GREEN_LIGHT },
            backgroundColorGetter = { 0xff303030.toInt() },
            headerColorGetter = { 0xff2e4b86.toInt() },
            iconColorGetter = { Color.WHITE }
    ),

    AMOLED(
            textResId = R.string.aardvark_amoled,
            textColorGetter = { Color.WHITE },
            accentColorGetter = { AARDVARK_GREEN_LIGHT },
            backgroundColorGetter = { Color.BLACK },
            headerColorGetter = { Color.BLACK },
            iconColorGetter = { Color.WHITE }
    ),

    CUSTOM(
            textResId = R.string.aardvark_custom,
            textColorGetter = { Prefs.customTextColor },
            accentColorGetter = { Prefs.customAccentColor },
            backgroundColorGetter = { Prefs.customBackgroundColor },
            headerColorGetter = { Prefs.customHeaderColor },
            iconColorGetter = { Prefs.customIconColor }
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

        const val AARDVARK_GREEN = 0xff4CAF50.toInt()
        const val AARDVARK_GREEN_LIGHT = 0xff80E27E.toInt()
    }
}