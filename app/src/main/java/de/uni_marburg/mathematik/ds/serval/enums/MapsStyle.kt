package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.RawRes
import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

enum class MapsStyle(@StringRes val titleRes: Int, @RawRes val style: Int) {

    STANDARD(R.string.maps_style_standard, R.raw.map_style_standard),
    SILVER(R.string.maps_style_silver, R.raw.map_style_silver),
    RETRO(R.string.maps_style_retro, R.raw.map_style_retro),
    DARK(R.string.maps_style_dark, R.raw.map_style_dark),
    NIGHT(R.string.maps_style_night, R.raw.map_style_night),
    AUBERGINE(R.string.maps_style_aubergine, R.raw.map_style_aubergine);

    companion object {
        val values = MapsStyle.values()
        operator fun invoke(index: Int) = values[index]
    }
}