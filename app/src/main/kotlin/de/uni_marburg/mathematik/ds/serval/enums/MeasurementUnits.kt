package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

enum class RadiationUnits(
    @StringRes val titleRes: Int,
    @StringRes val unitRes: Int
) {

    REM(
        titleRes = R.string.radiation_unit_rem,
        unitRes = R.string.radiation_unit_rem_unit
    ),

    MILLIREM(
        titleRes = R.string.radiation_unit_millirem,
        unitRes = R.string.radiation_unit_millirem_unit
    ),

    MILLISIEVERT(
        titleRes = R.string.radiation_unit_millisievert,
        unitRes = R.string.radiation_unit_millisievert_unit
    ),

    SIEVERT(
        titleRes = R.string.radiation_unit_sievert,
        unitRes = R.string.radiation_unit_sievert_unit
    ),

    BANANA_EQUIVALENT_DOSE(
        titleRes = R.string.radiation_unit_banana_equivalent_dose,
        unitRes = R.string.radiation_unit_banana_equivalent_dose_unit
    ),

    MICROSIEVERT(
        titleRes = R.string.radiation_unit_microsievert,
        unitRes = R.string.radiation_unit_microsievert_unit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}

enum class TemperatureUnits(
    @StringRes val titleRes: Int,
    @StringRes val unitRes: Int
) {

    CELSIUS(
        titleRes = R.string.temperature_unit_celsius,
        unitRes = R.string.temperature_unit_celsius_unit
    ),

    FAHRENHEIT(
        titleRes = R.string.temperature_unit_fahrenheit,
        unitRes = R.string.temperature_unit_fahrenheit_unit
    ),

    KELVIN(
        titleRes = R.string.temperature_unit_kelvin,
        unitRes = R.string.temperature_unit_kelvin_unit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}