package de.uni_marburg.mathematik.ds.serval.enums

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.utils.string
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Open source libraries used in Aardvark.
 *
 * @property authorRes Resource ID of the author
 * @property libraryDescriptionRes Resource ID of the library description
 * @property libraryNameRes Resource ID of the library name
 * @property libraryVersion Version of the library
 * @property licenseNameRes Resource ID of the name of the libraries license
 * @property licenseWebsiteRes Resource ID of the website of the libraries license
 * @property repositoryLinkRes Resource ID of the repository link
 */
enum class OpenSourceLibrary(
    @StringRes val authorRes: Int,
    @StringRes val libraryDescriptionRes: Int? = null,
    @StringRes val libraryNameRes: Int,
    val libraryVersion: String,
    @StringRes val licenseNameRes: Int,
    @StringRes val licenseWebsiteRes: Int,
    @StringRes val repositoryLinkRes: Int
) {

    KERVAL(
        authorRes = R.string.developer_name_kerval,
        libraryNameRes = R.string.kerval_name,
        libraryVersion = BuildConfig.KERVAL_VERSION,
        licenseNameRes = R.string.mit_license,
        licenseWebsiteRes = R.string.license_website_kerval,
        repositoryLinkRes = R.string.repository_link_kerval
    );

    fun getLibrary(context: Context): Library = with(context) {
        return Library().apply {
            author = string(authorRes)
            libraryDescription =
                    if (libraryDescriptionRes != null) string(libraryDescriptionRes) else ""
            libraryName = string(libraryNameRes)
            libraryVersion = this@OpenSourceLibrary.libraryVersion
            license = License().apply {
                licenseName = string(licenseNameRes)
                licenseWebsite = string(licenseWebsiteRes)
            }
            repositoryLink = string(repositoryLinkRes)
        }
    }
}
