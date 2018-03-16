package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R

enum class PrecipitationUnits(
    @StringRes val titleRes: Int,
    @StringRes val unitRes: Int
) {

    MILLIMETER(
        titleRes = R.string.precipitation_unit_millimeter,
        unitRes = R.string.precipitation_unit_millimeter_unit
    ),

    INCHES(
        titleRes = R.string.precipitation_unit_inches,
        unitRes = R.string.precipitation_unit_inches_unit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}

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

enum class WindUnits(
    @StringRes val titleRes: Int,
    @StringRes val unitRes: Int
) {

    METRES(
        titleRes = R.string.wind_unit_metres,
        unitRes = R.string.wind_unit_metres_unit
    ),

    MILES(
        titleRes = R.string.wind_unit_miles,
        unitRes = R.string.wind_unit_miles_unit
    ),

    KILOMETRES(
        titleRes = R.string.wind_unit_kilometres,
        unitRes = R.string.wind_unit_kilometres_unit
    ),

    NAUTICAL_KNOTS(
        titleRes = R.string.wind_unit_nautical_knots,
        unitRes = R.string.wind_unit_nautical_knots_unit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}