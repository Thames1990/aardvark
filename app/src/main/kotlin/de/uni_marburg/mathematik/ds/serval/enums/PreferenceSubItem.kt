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

    APPEARANCE(
        titleRes = R.string.preference_appearance,
        descRes = R.string.preference_appearance_desc,
        iicon = GoogleMaterial.Icon.gmd_palette
    ),

    BEHAVIOUR(
        titleRes = R.string.preference_behaviour,
        descRes = R.string.preference_behaviour_desc,
        iicon = GoogleMaterial.Icon.gmd_settings
    ),

    LOCATION(
        titleRes = R.string.location,
        descRes = R.string.location_desc,
        iicon = GoogleMaterial.Icon.gmd_my_location
    ),

    SERVAL(
        titleRes = R.string.preference_serval,
        descRes = R.string.preference_serval_desc,
        iicon = GoogleMaterial.Icon.gmd_network_wifi
    );
}

enum class DebugPreferenceSubItem(
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val iicon: IIcon
) {

    DEBUG(
        titleRes = R.string.preference_debug,
        descRes = R.string.preference_debug_desc,
        iicon = CommunityMaterial.Icon.cmd_android_debug_bridge
    )
}