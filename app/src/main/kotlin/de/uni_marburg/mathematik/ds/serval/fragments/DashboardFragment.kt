package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.bindViewResettable
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.string
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.expand
import de.uni_marburg.mathematik.ds.serval.utils.updateLayoutParams
import org.jetbrains.anko.displayMetrics

class DashboardFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_dashboard

    private val header: TextView by bindViewResettable(R.id.header)
    private val image: ImageView by bindViewResettable(R.id.image)
    private val description: TextView by bindViewResettable(R.id.description)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        header.setTextColor(AppearancePrefs.Theme.textColor)
        image.setIcon(
            icon = GoogleMaterial.Icon.gmd_sentiment_very_satisfied,
            color = AppearancePrefs.Theme.textColor,
            sizeDp = view.context.displayMetrics.densityDpi
        )
        description.setTextColor(AppearancePrefs.Theme.textColor)
    }

    override fun onSelected(
        appBarLayout: AppBarLayout,
        toolbar: Toolbar,
        fab: FloatingActionButton
    ) {
        appBarLayout.expand()
        with(toolbar) {
            updateLayoutParams<AppBarLayout.LayoutParams> { scrollFlags = 0 }
            title = context.string(R.string.tab_item_dashboard)
        }
        fab.hide()
    }

    override fun onReselected() = Unit

}
