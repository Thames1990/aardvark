package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.RawRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Defines the style of the map.
 *
 * @property titleRes Resource ID of the title
 * @property styleRes Resource ID to the JSON file describing the style
 */
enum class MapStyles(@StringRes val titleRes: Int, @RawRes val styleRes: Int) {

    STANDARD(
        titleRes = R.string.map_style_standard,
        styleRes = R.raw.map_style_standard
    ),

    SILVER(
        titleRes = R.string.map_style_silver,
        styleRes = R.raw.map_style_silver
    ),

    RETRO(
        titleRes = R.string.map_style_retro,
        styleRes = R.raw.map_style_retro
    ),

    DARK(
        titleRes = R.string.map_style_dark,
        styleRes = R.raw.map_style_dark
    ),

    NIGHT(
        titleRes = R.string.map_style_night,
        styleRes = R.raw.map_style_night
    ),

    AUBERGINE(
        titleRes = R.string.map_style_aubergine,
        styleRes = R.raw.map_style_aubergine
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}