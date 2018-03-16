package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

enum class TemperatureUnits(
    @StringRes val titleRes: Int,
    @StringRes val unit: Int
) {

    CELSIUS(
        titleRes = R.string.temperature_unit_celsius,
        unit = R.string.temperature_unit_celsius_unit
    ),

    FAHRENHEIT(
        titleRes = R.string.temperature_unit_fahrenheit,
        unit = R.string.temperature_unit_fahrenheit_unit
    ),

    KELVIN(
        titleRes = R.string.temperature_unit_kelvin,
        unit = R.string.temperature_unit_kelvin_unit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}