package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

enum class AboutItem(@StringRes val linkResId: Int, val iicon: IIcon) {
    GITHUB(
        linkResId = R.string.github_url,
        iicon = CommunityMaterial.Icon.cmd_github_circle
    )
}