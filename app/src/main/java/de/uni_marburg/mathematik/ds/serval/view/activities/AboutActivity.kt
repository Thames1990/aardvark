package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.setCurrentScreen
import org.jetbrains.anko.toast

class AboutActivity : AboutActivityBase(R.string::class.java, {
    textColor = Prefs.textColor
    accentColor = Prefs.accentColor
    backgroundColor = Prefs.bgColor
    cutoutForeground = Prefs.colorPrimary
    cutoutDrawableRes = R.drawable.aardvark
    faqPageTitleRes = R.string.faq_title
    faqXmlRes = R.xml.aardvark_faq
    faqParseNewLine = false
}) {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentScreen()
    }

    private var lastClick = -1L
    private var clickCount = 0

    override fun postInflateMainPage(adapter: FastItemThemedAdapter<IItem<*, *>>) {
        val aardvark = Library().apply {
            libraryName = string(R.string.aardvark_name)
            author = string(R.string.dev_name)
            libraryWebsite = string(R.string.github_url)
            isOpenSource = true
            libraryDescription = string(R.string.aardvark_description)
            libraryVersion = BuildConfig.VERSION_NAME
            license = License().apply {
                licenseName = getString(R.string.license_name)
                licenseWebsite = getString(R.string.license_website)
            }
        }
        adapter.add(LibraryIItem(aardvark)).add(AboutLinks())
        adapter.withOnClickListener { _, _, item, _ ->
            if (item is LibraryIItem) {
                val now = System.currentTimeMillis()
                if (now - lastClick > 500) clickCount = 0
                else clickCount++
                lastClick = now
                if (clickCount == 7 && !Prefs.debugSettings) {
                    Prefs.debugSettings = true
                    toast(R.string.debug_enabled)
                }
            }
            false
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
            with(holder) {
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
                images = arrayOf<Pair<IIcon, () -> Unit>>(
                        CommunityMaterial.Icon.cmd_github_circle to {
                            context.startLink(R.string.github_url)
                        },
                        CommunityMaterial.Icon.cmd_github_circle to {
                            context.startLink(R.string.github_url)
                        },
                        CommunityMaterial.Icon.cmd_github_circle to {
                            context.startLink(R.string.github_url)
                        },
                        CommunityMaterial.Icon.cmd_github_circle to {
                            context.startLink(R.string.github_url)
                        }
                ).mapIndexed { i, (icon, onClick) ->
                    ImageView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(size, size)
                        id = 109389 + i
                        setImageDrawable(icon.toDrawable(context, 32))
                        scaleType = ImageView.ScaleType.CENTER
                        background = context.resolveDrawable(
                                android.R.attr.selectableItemBackgroundBorderless
                        )
                        setOnClickListener { onClick() }
                        container.addView(this)
                    }
                }

                // Avoid problems with constraint chains
                if (images.size >= 2) {
                    val set = ConstraintSet()
                    set.clone(container)
                    set.createHorizontalChain(
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.LEFT,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.RIGHT,
                            images.map { it.id }.toIntArray(),
                            null,
                            ConstraintSet.CHAIN_SPREAD_INSIDE
                    )
                    set.applyTo(container)
                }
            }
        }
    }
}