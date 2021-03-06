package de.uni_marburg.mathematik.ds.serval.enums

import android.content.Context
import android.support.annotation.StringRes
import ca.allanwang.kau.about.LibraryIItem
import ca.allanwang.kau.utils.string
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Open source libraries used in Aardvark.
 *
 * @property authorRes Resource ID of the author
 * @property descriptionRes Resource ID of the library description
 * @property nameRes Resource ID of the library name
 * @property version Version of the library
 * @property licenseNameRes Resource ID of the name of the libraries license
 * @property licenseWebsiteRes Resource ID of the website of the libraries license
 * @property repositoryLinkRes Resource ID of the repository link
 */
enum class LibraryDefinitions(
    @StringRes val authorRes: Int,
    @StringRes val descriptionRes: Int? = null,
    @StringRes val nameRes: Int,
    val version: String,
    @StringRes val licenseNameRes: Int,
    @StringRes val licenseWebsiteRes: Int,
    @StringRes val repositoryLinkRes: Int
) {

    AARDVARK(
        authorRes = R.string.developer_name_aardvark,
        descriptionRes = R.string.aardvark_desc,
        nameRes = R.string.aardvark_name,
        version = BuildConfig.VERSION_NAME,
        licenseNameRes = R.string.license_mit,
        licenseWebsiteRes = R.string.license_website_aardvark,
        repositoryLinkRes = R.string.repository_link_aardvark
    ),

    KERVAL(
        authorRes = R.string.developer_name_kerval,
        descriptionRes = R.string.kerval_desc,
        nameRes = R.string.kerval_name,
        version = BuildConfig.KERVAL_VERSION,
        licenseNameRes = R.string.license_mit,
        licenseWebsiteRes = R.string.license_website_kerval,
        repositoryLinkRes = R.string.repository_link_kerval
    );

    fun getLibraryIItem(context: Context): LibraryIItem = with(context) {
        val library = Library().apply {
            author = string(authorRes)
            libraryDescription = descriptionRes?.let { string(it) }
            libraryName = string(nameRes)
            libraryVersion = version
            license = License().apply {
                licenseName = string(licenseNameRes)
                licenseWebsite = string(licenseWebsiteRes)
            }
            repositoryLink = string(repositoryLinkRes)
        }
        return LibraryIItem(library)
    }

    companion object {
        fun getAllLibraries(context: Context): List<Library> = with(context) {
            values()
                .filter { it != AARDVARK }
                .map { libraryDefinition ->
                    Library().apply {
                        author = string(libraryDefinition.authorRes)
                        libraryDescription = libraryDefinition.descriptionRes?.let { string(it) }
                        libraryName = string(libraryDefinition.nameRes)
                        libraryVersion = libraryDefinition.version
                        license = License().apply {
                            licenseName = string(libraryDefinition.licenseNameRes)
                            licenseWebsite = string(libraryDefinition.licenseWebsiteRes)
                        }
                        repositoryLink = string(libraryDefinition.repositoryLinkRes)
                    }
                }
        }
    }

}
