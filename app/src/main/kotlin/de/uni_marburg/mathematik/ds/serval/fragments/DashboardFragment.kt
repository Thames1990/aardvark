package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.google.android.gms.location.DetectedActivity
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentContext
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.displayMetrics


class DashboardFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_dashboard

    private lateinit var activityRecognitionControl: SmartLocation.ActivityRecognitionControl

    private var detectedActivity: DetectedActivity? = null

    private val title: TextView by bindView(R.id.title)
    private val image: ImageView by bindView(R.id.image)
    private val description: TextView by bindView(R.id.description)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRecognitionControl = SmartLocation.with(currentContext).activity()
    }

    override fun onResume() {
        super.onResume()
        activityRecognitionControl.start { activity ->
            if (detectedActivity != activity) {
                detectedActivity = activity
                if (Prefs.animate) {
                    image.fadeScaleTransition {
                        image.setIcon(
                            icon = activity.iicon,
                            color = Prefs.textColor,
                            sizeDp = currentContext.displayMetrics.densityDpi
                        )
                    }
                    description.setTextWithFade(activity.currentActivity)
                } else {
                    image.setIcon(
                        icon = activity.iicon,
                        color = Prefs.textColor,
                        sizeDp = currentContext.displayMetrics.densityDpi
                    )
                    description.text = activity.currentActivity
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        activityRecognitionControl.stop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.setTextColor(Prefs.textColor)
        image.setIcon(
            icon = GoogleMaterial.Icon.gmd_sentiment_very_satisfied,
            color = Prefs.textColor,
            sizeDp = currentContext.displayMetrics.densityDpi
        )
        description.setTextColor(Prefs.textColor)
    }

    private inline val DetectedActivity.currentActivity: String
        get() {
            val formatId = R.string.activity_desc
            val valueId = when (type) {
                DetectedActivity.IN_VEHICLE -> R.string.activity_in_vehicle
                DetectedActivity.ON_BICYCLE -> R.string.activity_on_bicycle
                DetectedActivity.ON_FOOT -> R.string.activity_on_foot
                DetectedActivity.STILL -> R.string.activity_still
                DetectedActivity.TILTING -> R.string.activity_tilting
                DetectedActivity.WALKING -> R.string.activity_walking
                DetectedActivity.RUNNING -> R.string.activity_running
                else -> R.string.activity_unknown
            }
            val format = currentContext.string(formatId)
            val value = currentContext.string(valueId)
            return String.format(format = format, args = *arrayOf(value))
        }

    private inline val DetectedActivity.iicon: IIcon
        get() = when (type) {
            DetectedActivity.IN_VEHICLE -> CommunityMaterial.Icon.cmd_car
            DetectedActivity.ON_BICYCLE -> CommunityMaterial.Icon.cmd_bike
            DetectedActivity.ON_FOOT -> CommunityMaterial.Icon.cmd_walk
            DetectedActivity.STILL -> CommunityMaterial.Icon.cmd_home
            DetectedActivity.TILTING -> CommunityMaterial.Icon.cmd_rotate_3d
            DetectedActivity.WALKING -> CommunityMaterial.Icon.cmd_walk
            DetectedActivity.RUNNING -> CommunityMaterial.Icon.cmd_run
            else -> CommunityMaterial.Icon.cmd_help
        }

}