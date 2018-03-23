package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.bindViewResettable
import ca.allanwang.kau.utils.setIcon
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import org.jetbrains.anko.displayMetrics

class DashboardFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_dashboard

    private val title: TextView by bindViewResettable(R.id.title)
    private val image: ImageView by bindViewResettable(R.id.image)
    private val description: TextView by bindViewResettable(R.id.description)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setTextColor(AppearancePrefs.Theme.textColor)
        image.setIcon(
            icon = GoogleMaterial.Icon.gmd_sentiment_very_satisfied,
            color = AppearancePrefs.Theme.textColor,
            sizeDp = view.context.displayMetrics.densityDpi
        )
        description.setTextColor(AppearancePrefs.Theme.textColor)
    }

}
