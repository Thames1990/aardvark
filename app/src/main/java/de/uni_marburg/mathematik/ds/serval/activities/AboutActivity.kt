package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.os.PersistableBundle
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
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AboutItem
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.aardvarkSnackbar
import de.uni_marburg.mathematik.ds.serval.utils.setCurrentScreen

class AboutActivity : AboutActivityBase(R.string::class.java, {
    textColor = Prefs.textColor
    accentColor = Prefs.accentColor
    backgroundColor = Prefs.backgroundColor.withMinAlpha(200)
    cutoutForeground = Prefs.accentColor
    cutoutDrawableRes = R.drawable.aardvark
    faqPageTitleRes = R.string.faq_title
    faqXmlRes = R.xml.aardvark_faq
    faqParseNewLine = false
}) {

    private var lastClick: Long = -1L
    private var clickCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setCurrentScreen()
    }

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        val aardvark = Library().apply {
            author = string(R.string.developer_name)
            isOpenSource = true
            libraryDescription = string(R.string.aardvark_description)
            libraryName = string(R.string.aardvark_name)
            libraryVersion = BuildConfig.VERSION_NAME
            libraryWebsite = string(R.string.github_url)
            license = License().apply {
                licenseName = getString(R.string.license_name)
                licenseWebsite = getString(R.string.license_website)
            }
        }
        adapter.apply {
            add(LibraryIItem(aardvark))
            add(AboutLinks())
            withOnClickListener { _, _, item, _ ->
                if (item is LibraryIItem) {
                    val now = System.currentTimeMillis()
                    // Only register clicks within a timespan of 500 milliseconds
                    if (now - lastClick > 500) clickCount = 0 else clickCount++
                    lastClick = now
                    // Enable debug settings if the user clicked 7 times in a short timespan
                    if (clickCount == 7 && !Prefs.debugSettings) {
                        Prefs.debugSettings = true
                        aardvarkSnackbar(R.string.debug_enabled)
                    }
                }
                false
            }
        }
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
                bindIconColor(*images.toTypedArray())
                bindBackgroundColor(container)
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

            val container: ConstraintLayout by bindView(R.id.about_icons_container)
            val images: List<ImageView>

            init {
                val context = itemView.context
                val size = context.dimenPixelSize(R.dimen.kau_avatar_bounds)

                images = AboutItem.values().mapIndexed { index, aboutItem ->
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
                if (images.size >= 2) {
                    ConstraintSet().apply {
                        clone(container)
                        createHorizontalChain(
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.RIGHT,
                            images.map { it.id }.toIntArray(),
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