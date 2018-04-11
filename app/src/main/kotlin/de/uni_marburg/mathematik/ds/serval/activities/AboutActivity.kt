package de.uni_marburg.mathematik.ds.serval.activities

import android.app.Activity
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
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.ExperimentalPrefs
import de.uni_marburg.mathematik.ds.serval.utils.withHorizontalChain

/**
 * Shows details about the application, used open-source libraries and frequently asked questions.
 */
class AboutActivity : AboutActivityBase(
    rClass = R.string::class.java,
    configBuilder = {
        accentColor = AppearancePrefs.Theme.accentColor
        backgroundColor = AppearancePrefs.Theme.backgroundColor.withMinAlpha(200)
        cutoutDrawableRes = R.drawable.aardvark
        cutoutForeground = AppearancePrefs.Theme.accentColor
        faqParseNewLine = false
        faqXmlRes = R.xml.faq
        textColor = AppearancePrefs.Theme.textColor
    }
) {

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        val aardvark: LibraryIItem = LibraryDefinitions.AARDVARK.getLibraryIItem(this)
        adapter.add(listOf<IItem<*, *>>(aardvark, AboutLinkIItem()))
        addExperimentalSettingsToggleListener(adapter, aardvark)
    }

    override fun getLibraries(libs: Libs): List<Library> {
        // Auto detect libraries
        val libraries: List<Library> = super.getLibraries(libs)
        // Manually add libraries
        val extendedLibraries: Set<Library> =
            libraries union LibraryDefinitions.getAllLibraries(this)
        return extendedLibraries.sortedBy { it.libraryName }
    }

    private fun addExperimentalSettingsToggleListener(
        adapter: FastItemThemedAdapter<IItem<*, *>>,
        aardvark: LibraryIItem
    ) = adapter.withOnRepeatedClickListener(
        count = REPEATED_CLICK_LISTENER_COUNT,
        duration = REPEATED_CLICK_LISTENER_DURATION,
        event = OnClickListener<IItem<*, *>> { _, _, item, _ ->
            if (item == aardvark) {
                if (!ExperimentalPrefs.enabled) {
                    ExperimentalPrefs.enabled = true
                    toast(R.string.preference_experimental_enabled)
                    setResult(Activity.RESULT_OK)
                } else {
                    toast(R.string.preference_experimental_already_enabled)
                }
                return@OnClickListener true
            }
            return@OnClickListener false
        }
    )

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
                        setIcon(icon = aboutLink.iicon, color = AppearancePrefs.Theme.iconColor)
                        setOnClickListener { context.startLink(aboutLink.linkRes) }
                        container.addView(this)
                    }
                }

                // Avoid problems with constraint chains
                if (items.size >= 2) {
                    ConstraintSet().apply {
                        clone(container)
                        withHorizontalChain(
                            leftId = ConstraintSet.PARENT_ID,
                            leftSide = ConstraintSet.LEFT,
                            rightId = ConstraintSet.PARENT_ID,
                            rightSide = ConstraintSet.RIGHT,
                            chainIds = items.map { it.id }.toIntArray(),
                            weights = null,
                            style = ConstraintSet.CHAIN_SPREAD_INSIDE
                        )
                        applyTo(container)
                    }
                }
            }
        }
    }

    companion object {
        const val REPEATED_CLICK_LISTENER_COUNT: Int = 7
        const val REPEATED_CLICK_LISTENER_DURATION: Long = 500L
    }

}