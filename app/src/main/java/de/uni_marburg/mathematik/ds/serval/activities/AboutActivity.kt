package de.uni_marburg.mathematik.ds.serval.activities

import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ca.allanwang.kau.about.AboutActivityBase
import ca.allanwang.kau.about.LibraryIItem
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.utils.*
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AardvarkLibrary
import de.uni_marburg.mathematik.ds.serval.enums.AboutItem
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInMillis
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

/**
 * Shows details about the application, used open-source libraries and frequently asked questions.
 */
class AboutActivity : AboutActivityBase(
    rClass = R.string::class.java,
    configBuilder = {
        textColor = Prefs.textColor
        accentColor = Prefs.accentColor
        backgroundColor = Prefs.backgroundColor.withMinAlpha(200)
        cutoutForeground = Prefs.accentColor
        cutoutDrawableRes = R.drawable.aardvark
        faqPageTitleRes = R.string.faq_title
        faqXmlRes = R.xml.aardvark_faq
        faqParseNewLine = false
    }
) {

    companion object {
        const val DEBUG_CLICK_TIMESPAN = 500L
        const val DEBUG_CLICK_COUNT = 7
    }

    /**
     * Saves the last time, the Aardvark library item was clicked.
     */
    private var lastClick: Long = -1L

    /**
     * Saves the click count on the Aardvark library item in
     * [a timespan of milliseconds][DEBUG_CLICK_TIMESPAN].
     */
    private var clickCount: Int = 0

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        val aardvark = Library().apply {
            author = string(R.string.developer_name_aardvark)
            libraryDescription = string(R.string.aardvark_description)
            libraryName = string(R.string.aardvark_name)
            libraryVersion = BuildConfig.VERSION_NAME
            license = License().apply {
                licenseName = string(R.string.mit_license)
                licenseWebsite = string(R.string.license_website_aardvark)
            }
            repositoryLink = string(R.string.repository_link_aardvark)
        }

        adapter.apply {
            add(LibraryIItem(aardvark))
            add(AboutLinks())
            withOnClickListener { _, _, item, _ ->
                if (item is LibraryIItem) {
                    val now = currentTimeInMillis
                    if (now - lastClick > DEBUG_CLICK_TIMESPAN) clickCount = 0 else clickCount++
                    lastClick = now
                    if (clickCount == DEBUG_CLICK_COUNT && !Prefs.debugSettings) {
                        Prefs.debugSettings = true
                        snackbarThemed(R.string.debug_enabled)
                    }
                }
                false
            }
        }
    }

    override fun getLibraries(libs: Libs): List<Library> {
        val libraries: MutableList<Library> = super.getLibraries(libs).toMutableList()
        AardvarkLibrary.values().map { library ->
            libraries.add(Library().apply {
                author = string(library.authorRes)
                libraryDescription = if (library.libraryDescriptionRes != null) {
                    string(library.libraryDescriptionRes)
                } else ""
                libraryName = string(library.libraryNameRes)
                libraryVersion = library.libraryVersion
                license = License().apply {
                    licenseName = string(library.licenseNameRes)
                    licenseWebsite = string(library.licenseWebsiteRes)
                }
                repositoryLink = string(library.repositoryLinkRes)
            })
        }
        return libraries.sortedBy { library -> library.libraryName }
    }

    class AboutLinks :
        AbstractItem<AboutLinks, AboutLinks.ViewHolder>(),
        ThemableIItem by ThemableIItemDelegate() {

        override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

        override fun getType(): Int = R.id.item_about_links

        override fun getLayoutRes(): Int = R.layout.item_about_links

        override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
            super.bindView(holder, payloads)
            holder.apply {
                bindIconColor(*items.toTypedArray())
                bindBackgroundColor(container)
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val container: ConstraintLayout by bindView(R.id.about_icons_container)
            val items: List<ImageView>

            init {
                val context = itemView.context
                val size = context.dimenPixelSize(R.dimen.kau_avatar_bounds)

                items = AboutItem.values().mapIndexed { index, aboutItem ->
                    ImageView(context).apply {
                        id = index
                        layoutParams = ViewGroup.LayoutParams(size, size)
                        scaleType = ImageView.ScaleType.CENTER
                        background = context.resolveDrawable(
                            android.R.attr.selectableItemBackgroundBorderless
                        )
                        setIcon(icon = aboutItem.iicon, color = Prefs.iconColor)
                        setOnClickListener { context.startLink(aboutItem.linkRes) }
                        container.addView(this)
                    }
                }

                // Avoid problems with constraint chains
                if (items.size >= 2) {
                    ConstraintSet().apply {
                        clone(container)
                        createHorizontalChain(
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.RIGHT,
                            items.map { it.id }.toIntArray(),
                            null,
                            ConstraintSet.CHAIN_SPREAD_INSIDE
                        )
                        applyTo(container)
                    }
                }
            }
        }
    }
}