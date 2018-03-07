package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Links that should be shown in the about section.
 *
 * @property linkRes Resource ID of the link
 * @property iicon Icon representing the link
 */
enum class AboutLink(@StringRes val linkRes: Int, val iicon: IIcon) {

    GITHUB(
        linkRes = R.string.repository_link_aardvark,
        iicon = CommunityMaterial.Icon.cmd_github_circle
    )

}