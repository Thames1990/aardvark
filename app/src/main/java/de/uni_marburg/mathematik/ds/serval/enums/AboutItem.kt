package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Created by thames1990 on 09.01.18.
 */
enum class AboutItem(val iicon: IIcon, @StringRes val linkResId: Int) {
    GITHUB(CommunityMaterial.Icon.cmd_github_circle, R.string.github_url)
}