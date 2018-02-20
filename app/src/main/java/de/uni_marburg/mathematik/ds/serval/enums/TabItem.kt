package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Items that should be shown in the tab layout.
 *
 * @property titleRes Resource ID of title of the tab item that is shown to the user.
 * @property iicon Icon of the tab item that is shown to the user.
 */
enum class TabItem(@StringRes val titleRes: Int, val iicon: IIcon) {

    DASHBOARD(
        titleRes = R.string.dashboard,
        iicon = GoogleMaterial.Icon.gmd_dashboard
    ),

    EVENTS(
        titleRes = R.string.events,
        iicon = GoogleMaterial.Icon.gmd_info_outline
    ),

    MAP(
        titleRes = R.string.map,
        iicon = GoogleMaterial.Icon.gmd_map
    )

}