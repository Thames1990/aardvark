package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R

// TODO Use in AboutActivity
enum class AardvarkLibrary(
    @StringRes val authorRes: Int,
    @StringRes val libraryDescriptionRes: Int? = null,
    @StringRes val libraryNameRes: Int,
    val libraryVersion: String,
    @StringRes val licenseNameRes: Int,
    @StringRes val licenseWebsiteRes: Int,
    @StringRes val repositoryLinkRes: Int
) {

    AARDVARK(
        authorRes = R.string.developer_name_aardvark,
        libraryDescriptionRes = R.string.aardvark_description,
        libraryNameRes = R.string.aardvark_name,
        libraryVersion = BuildConfig.VERSION_NAME,
        licenseNameRes = R.string.mit_license,
        licenseWebsiteRes = R.string.license_website_aardvark,
        repositoryLinkRes = R.string.repository_link_aardvark
    ),

    KERVAL(
        authorRes = R.string.developer_name_kerval,
        libraryNameRes = R.string.kerval_name,
        libraryVersion = BuildConfig.KERVAL_VERSION,
        licenseNameRes = R.string.mit_license,
        licenseWebsiteRes = R.string.license_website_kerval,
        repositoryLinkRes = R.string.repository_link_kerval
    );

    companion object {
        val values = values()
        operator fun invoke(index: Int) = values[index]
    }
}
