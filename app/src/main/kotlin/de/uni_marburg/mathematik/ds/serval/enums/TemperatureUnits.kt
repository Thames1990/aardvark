package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

enum class TemperatureUnits(
    @StringRes val titleRes: Int,
    val iicon: IIcon
) {

    CELSIUS(
        titleRes = R.string.temperature_unit_celsius,
        iicon = CommunityMaterial.Icon.cmd_temperature_celsius
    ),

    FAHRENHEIT(
        titleRes = R.string.temperature_unit_fahrenheit,
        iicon = CommunityMaterial.Icon.cmd_temperature_fahrenheit
    ),

    KELVIN(
        titleRes = R.string.temperature_unit_kelvin,
        iicon = CommunityMaterial.Icon.cmd_temperature_fahrenheit
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }

}