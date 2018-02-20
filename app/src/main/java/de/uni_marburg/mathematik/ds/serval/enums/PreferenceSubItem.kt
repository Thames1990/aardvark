package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Preference sub items.
 *
 * Those structure the settings into logical or meaningful parts.
 *
 * @property titleRes Resource ID of the title
 * @property descRes Resource ID of the description
 * @property iicon Icon shown on the left side
 */
enum class PreferenceSubItem(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val iicon: IIcon
) {

    BEHAVIOUR(
        titleRes = R.string.behaviour,
        descRes = R.string.behaviour_description,
        iicon = GoogleMaterial.Icon.gmd_settings
    ),

    APPEARANCE(
        titleRes = R.string.appearance,
        descRes = R.string.appearance_description,
        iicon = GoogleMaterial.Icon.gmd_palette
    ),

    LOCATION(
        titleRes = R.string.location,
        descRes = R.string.location_description,
        iicon = GoogleMaterial.Icon.gmd_my_location
    ),

    SERVAL(
        titleRes = R.string.serval,
        descRes = R.string.serval_description,
        iicon = GoogleMaterial.Icon.gmd_network_wifi
    );
}

enum class DebugPreferenceSubItem(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val iicon: IIcon
) {

    DEBUG(
        titleRes = R.string.debug,
        descRes = R.string.debug_description,
        iicon = CommunityMaterial.Icon.cmd_android_debug_bridge
    )
}