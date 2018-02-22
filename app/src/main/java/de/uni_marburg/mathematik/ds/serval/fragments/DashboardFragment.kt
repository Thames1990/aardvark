package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.string
import com.google.android.gms.location.DetectedActivity
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentContext
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.displayMetrics


class DashboardFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_dashboard

    private val title: TextView by bindView(R.id.title)
    private val image: ImageView by bindView(R.id.image)
    private val description: TextView by bindView(R.id.description)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setTextColor(Prefs.textColor)
        image.setIcon(
            icon = GoogleMaterial.Icon.gmd_sentiment_very_satisfied,
            color = Prefs.textColor,
            sizeDp = currentContext.displayMetrics.densityDpi
        )
        description.setTextColor(Prefs.textColor)

        SmartLocation.with(currentContext).activity().start { detectedActivity ->
            description.text = detectedActivity.typeString
        }
    }

    private inline val DetectedActivity.typeString: String
        get() = with(currentContext) {
            when (type) {
                DetectedActivity.IN_VEHICLE -> string(R.string.activity_in_vehicle)
                DetectedActivity.ON_BICYCLE -> string(R.string.activity_on_bicycle)
                DetectedActivity.ON_FOOT -> string(R.string.activity_on_foot)
                DetectedActivity.STILL -> string(R.string.activity_still)
                DetectedActivity.TILTING -> string(R.string.activity_tilting)
                else -> string(R.string.activityUnknown)
            }
        }
}
