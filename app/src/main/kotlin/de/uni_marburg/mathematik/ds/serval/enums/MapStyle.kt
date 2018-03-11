package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.RawRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Defines the style of the map.
 *
 * @property titleRes Resource ID of the title
 * @property style Resource ID to the JSON file describing the style
 */
enum class MapStyle(@StringRes val titleRes: Int, @RawRes val style: Int) {

    STANDARD(
        titleRes = R.string.maps_style_standard,
        style = R.raw.map_style_standard
    ),

    SILVER(
        titleRes = R.string.maps_style_silver,
        style = R.raw.map_style_silver
    ),

    RETRO(
        titleRes = R.string.maps_style_retro,
        style = R.raw.map_style_retro
    ),

    DARK(
        titleRes = R.string.maps_style_dark,
        style = R.raw.map_style_dark
    ),

    NIGHT(
        titleRes = R.string.maps_style_night,
        style = R.raw.map_style_night
    ),

    AUBERGINE(
        titleRes = R.string.maps_style_aubergine,
        style = R.raw.map_style_aubergine
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}