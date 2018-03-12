package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Items that should be shown in the tab layout.
 *
 * @property titleRes Resource ID of title of the tab item that is shown to the user.
 * @property iicon Icon of the tab item that is shown to the user.
 */
enum class TabItems(@StringRes val titleRes: Int, val iicon: IIcon) {

    DASHBOARD(
        titleRes = R.string.tab_item_dashboard,
        iicon = GoogleMaterial.Icon.gmd_dashboard
    ),

    EVENTS(
        titleRes = R.string.tab_item_events,
        iicon = GoogleMaterial.Icon.gmd_view_list
    ),

    MAP(
        titleRes = R.string.tab_item_map,
        iicon = CommunityMaterial.Icon.cmd_google_maps
    )

}