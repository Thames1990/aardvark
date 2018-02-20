package de.uni_marburg.mathematik.ds.serval.enums

import android.graphics.Color
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/**
 * Defines a layout theme.
 *
 * @property titleRes Resource ID of the title
 * @property textColor Color used for text
 * @property accentColor Color used to call attention to key elements
 * @property backgroundColor Color used for backgrounds
 * @property headerColor Color used for the toolbar
 * @property iconColor Color used for icons
 */
enum class Theme(
    @StringRes val titleRes: Int,
    private val textColorGetter: () -> Int,
    private val accentColorGetter: () -> Int,
    private val backgroundColorGetter: () -> Int,
    private val headerColorGetter: () -> Int,
    private val iconColorGetter: () -> Int
) {

    LIGHT(
        titleRes = R.string.aardvark_light,
        textColorGetter = Color::BLACK,
        accentColorGetter = ::FRUIT_SALAD,
        backgroundColorGetter = ::ALABASTER,
        headerColorGetter = ::FRUIT_SALAD,
        iconColorGetter = Color::WHITE
    ),

    DARK(
        titleRes = R.string.aardvark_dark,
        textColorGetter = { Color.WHITE },
        accentColorGetter = ::PASTEL_GREEN,
        backgroundColorGetter = ::MINE_SHAFT,
        headerColorGetter = ::CHAMBRAY,
        iconColorGetter = Color::WHITE
    ),

    AMOLED(
        titleRes = R.string.aardvark_amoled,
        textColorGetter = Color::WHITE,
        accentColorGetter = ::PASTEL_GREEN,
        backgroundColorGetter = Color::BLACK,
        headerColorGetter = Color::BLACK,
        iconColorGetter = Color::WHITE
    ),

    CUSTOM(
        titleRes = R.string.aardvark_custom,
        textColorGetter = Prefs::customTextColor,
        accentColorGetter = Prefs::customAccentColor,
        backgroundColorGetter = Prefs::customBackgroundColor,
        headerColorGetter = Prefs::customHeaderColor,
        iconColorGetter = Prefs::customIconColor
    );

    val accentColor: Int
        get() = accentColorGetter()

    val backgroundColor: Int
        get() = backgroundColorGetter()

    val headerColor: Int
        get() = headerColorGetter()

    val iconColor: Int
        get() = iconColorGetter()

    val textColor: Int
        get() = textColorGetter()

    companion object {
        operator fun invoke(index: Int) = values()[index]

        const val ALABASTER = 0xfffafafa.toInt()
        const val CHAMBRAY = 0xff2e4b86.toInt()
        const val FRUIT_SALAD = 0xff4CAF50.toInt()
        const val MINE_SHAFT = 0xff303030.toInt()
        const val PASTEL_GREEN = 0xff80E27E.toInt()
        const val PORCELAIN = 0xffeceff1.toInt()
        const val LOCHMARA = 0xff0288d1.toInt()
        const val BAHAMA_BLUE = 0xff01579b.toInt()

        const val AARDVARK_GREEN = FRUIT_SALAD
    }
}