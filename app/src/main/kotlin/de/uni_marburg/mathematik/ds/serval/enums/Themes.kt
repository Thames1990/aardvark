package de.uni_marburg.mathematik.ds.serval.enums

import android.graphics.Color
import android.support.annotation.RawRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs.Theme.Custom
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs

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
enum class Themes(
    @StringRes val titleRes: Int,
    @RawRes val mapStyle: Int,
    private val textColorGetter: () -> Int,
    private val accentColorGetter: () -> Int,
    private val backgroundColorGetter: () -> Int,
    private val headerColorGetter: () -> Int,
    private val iconColorGetter: () -> Int
) {

    LIGHT(
        titleRes = R.string.kau_light,
        mapStyle = R.raw.map_style_standard,
        textColorGetter = Color::BLACK,
        accentColorGetter = ::FRUIT_SALAD,
        backgroundColorGetter = ::ALABASTER,
        headerColorGetter = ::FRUIT_SALAD,
        iconColorGetter = Color::WHITE
    ),

    DARK(
        titleRes = R.string.kau_dark,
        mapStyle = R.raw.map_style_dark,
        textColorGetter = Color::WHITE,
        accentColorGetter = ::PASTEL_GREEN,
        backgroundColorGetter = ::MINE_SHAFT,
        headerColorGetter = ::CHAMBRAY,
        iconColorGetter = Color::WHITE
    ),

    AMOLED(
        titleRes = R.string.kau_amoled,
        mapStyle = R.raw.map_style_night,
        textColorGetter = Color::WHITE,
        accentColorGetter = ::PASTEL_GREEN,
        backgroundColorGetter = Color::BLACK,
        headerColorGetter = Color::BLACK,
        iconColorGetter = Color::WHITE
    ),

    CUSTOM(
        titleRes = R.string.kau_custom,
        mapStyle = MapPrefs.MapStyle.styleRes,
        textColorGetter = Custom::textColor,
        accentColorGetter = Custom::accentColor,
        backgroundColorGetter = Custom::bgColor,
        headerColorGetter = Custom::headerColor,
        iconColorGetter = Custom::iconColor
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

        const val ALABASTER = 0xFFFAFAFA.toInt()
        const val BAHAMA_BLUE = 0xFF01579B.toInt()
        const val CHAMBRAY = 0xFF2E4B86.toInt()
        const val FRUIT_SALAD = 0xFF4CAF50.toInt()
        const val LOCHMARA = 0xFF0288D1.toInt()
        const val MINE_SHAFT = 0xFF303030.toInt()
        const val PASTEL_GREEN = 0xFF80E27E.toInt()
        const val PORCELAIN = 0xFFECEFF1.toInt()

        const val AARDVARK_GREEN = FRUIT_SALAD
    }
}