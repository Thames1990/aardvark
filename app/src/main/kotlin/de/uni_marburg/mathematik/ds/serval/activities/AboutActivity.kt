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
import ca.allanwang.kau.adapters.withOnRepeatedClickListener
import ca.allanwang.kau.utils.*
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.OnClickListener
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AboutLinks
import de.uni_marburg.mathematik.ds.serval.enums.LibraryDefinitions
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

/**
 * Shows details about the application, used open-source libraries and frequently asked questions.
 */
class AboutActivity : AboutActivityBase(
    rClass = R.string::class.java,
    configBuilder = {
        accentColor = Prefs.Appearance.Theme.accentColor
        backgroundColor = Prefs.Appearance.Theme.backgroundColor.withMinAlpha(200)
        cutoutDrawableRes = R.drawable.aardvark
        cutoutForeground = Prefs.Appearance.Theme.accentColor
        faqParseNewLine = false
        faqXmlRes = R.xml.faq
        textColor = Prefs.Appearance.Theme.textColor
    }
) {

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        val aardvark: LibraryIItem = LibraryDefinitions.AARDVARK.getLibraryIItem(context = this)

        with(adapter) {
            add(aardvark)
            add(AboutLinkIItem())

            // Activate debug settings, if the user clicked the Aardvark item multiple times in
            // a short duration
            withOnRepeatedClickListener(
                count = 7,
                duration = 500L,
                event = OnClickListener<IItem<*, *>> { _, _, item, _ ->
                    if (!Prefs.Experimental.enabled && item == aardvark) {
                        Prefs.Experimental.enabled = true
                        snackbarThemed(
                            textRes = R.string.preference_experimental_enabled,
                            builder = {
                                // Add option to reload settings to activate experimental settings
                                setAction(
                                    R.string.preference_reload,
                                    { startActivity<SettingsActivity>() }
                                )
                            }
                        )
                    }
                    true
                }
            )
        }
    }

    override fun getLibraries(libs: Libs): List<Library> {
        // Auto detect libraries
        val libraries: List<Library> = super.getLibraries(libs)
        // Manually add libraries
        val extendedLibraries = libraries union LibraryDefinitions.getAllLibraries(context = this)
        return extendedLibraries.sortedBy { it.libraryName }
    }

    private class AboutLinkIItem :
        AbstractItem<AboutLinkIItem, AboutLinkIItem.ViewHolder>(),
        ThemableIItem by ThemableIItemDelegate() {

        override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

        override fun getType(): Int = R.id.item_about_links

        override fun getLayoutRes(): Int = R.layout.item_about_links

        override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
            super.bindView(holder, payloads)
            with(holder) {
                bindIconColor(*items.toTypedArray())
                bindBackgroundColor(container)
            }
        }

        private class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val container: ConstraintLayout by bindView(R.id.about_icons_container)
            val items: List<ImageView>

            init {
                val context = itemView.context
                val size = context.dimenPixelSize(R.dimen.kau_avatar_bounds)

                items = AboutLinks.values().mapIndexed { index, aboutLink ->
                    ImageView(context).apply {
                        id = index
                        layoutParams = ViewGroup.LayoutParams(size, size)
                        scaleType = ImageView.ScaleType.CENTER
                        background = context.resolveDrawable(
                            android.R.attr.selectableItemBackgroundBorderless
                        )
                        setIcon(icon = aboutLink.iicon, color = Prefs.Appearance.Theme.iconColor)
                        setOnClickListener { context.startLink(aboutLink.linkRes) }
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