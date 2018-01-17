package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/** Created by thames1990 on 04.12.17. */
enum class AardvarkItem(@StringRes val titleResId: Int, val icon: IIcon) {
    DASHBOARD(
        titleResId = R.string.dashboard,
        icon = GoogleMaterial.Icon.gmd_dashboard
    ),
    EVENTS(
        titleResId = R.string.events,
        icon = GoogleMaterial.Icon.gmd_info_outline
    ),
    MAP(
        titleResId = R.string.map,
        icon = GoogleMaterial.Icon.gmd_map
    )
}