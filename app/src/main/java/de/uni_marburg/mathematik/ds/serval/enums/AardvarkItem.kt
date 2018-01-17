package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

enum class AardvarkItem(@StringRes val titleRes: Int, val iicon: IIcon) {
    DASHBOARD(R.string.dashboard, GoogleMaterial.Icon.gmd_dashboard),
    EVENTS(R.string.events, GoogleMaterial.Icon.gmd_info_outline),
    MAP(R.string.map, GoogleMaterial.Icon.gmd_map)
}