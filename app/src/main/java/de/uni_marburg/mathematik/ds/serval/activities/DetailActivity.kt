package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.net.Uri
import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.SupportTopic
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventDatabase
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.MapIItem
import de.uni_marburg.mathematik.ds.serval.views.SmallHeaderIItem
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Displays all details of an [event][Event].
 */
class DetailActivity : ElasticRecyclerActivity() {

    private lateinit var event: Event
    private lateinit var geocodingControl: SmartLocation.GeocodingControl

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        setSecureFlag()
        setColors {
            header(appBar)
            toolbar(toolbar)
            themeWindow = false
        }

        geocodingControl = SmartLocation.with(this).geocoding()

        doAsync {
            event = EventDatabase
                .get(this@DetailActivity)
                .eventDao()
                .getById(intent.extras.getString(EVENT_ID))

            uiThread {
                setupAdapter()
                fab.apply {
                    backgroundTintList = ColorStateList.valueOf(Prefs.accentColor)
                    setIcon(icon = GoogleMaterial.Icon.gmd_navigation, color = Prefs.iconColor)
                    setOnClickListener { showInGoogleMaps() }
                    show()
                }
            }
        }

        setOutsideTapListener { finishAfterTransition() }

        return true
    }

    override fun onPause() {
        super.onPause()
        geocodingControl.stop()
    }

    private fun setupAdapter() {
        val adapter = FastItemAdapter<IItem<*, *>>()
        val showMap: Boolean = intent.extras.getBoolean(SHOW_MAP)

        if (::event.isInitialized) {
            title = event.title

            val measurementCardItems = mutableListOf<CardIItem>()
            event.measurements.forEach { measurement ->
                val format = string(measurement.type.formatRes)
                val measurementDescription = String.format(format, measurement.value)
                val measurementCardItem = CardIItem {
                    titleRes = measurement.type.titleRes
                    desc = measurementDescription
                    imageIIcon = measurement.type.iicon
                    imageIIconColor = Prefs.iconColor
                }
                measurementCardItems.add(measurementCardItem)
            }

            val eventCardItem = CardIItem {
                titleRes = R.string.time
                val passedTime: String = event.passedTime.timeToString(this@DetailActivity)
                desc = "${event.snippet}\n$passedTime"
                imageIIcon = GoogleMaterial.Icon.gmd_access_time
            }

            geocodingControl.reverse(event.location) { _, results ->
                if (results.isNotEmpty()) {
                    val address: Address = results[0]
                    val addressLine: String = address.getAddressLine(0)
                    val addressLines: String = addressLine
                        .replace(oldValue = "unnamed road, ", newValue = "", ignoreCase = true)
                        .replace(oldValue = ", ", newValue = "\n")
                    val addressCard = CardIItem {
                        titleRes = R.string.location_address
                        desc = addressLines
                        imageIIcon = CommunityMaterial.Icon.cmd_map_marker
                    }
                    adapter.add(addressCard)
                }
            }

            with(adapter) {
                if (showMap) add(MapIItem(event))
                add(
                    SmallHeaderIItem(
                        text = plural(
                            R.plurals.measurement,
                            quantity = event.measurements.count()
                        )
                    )
                )
                measurementCardItems.forEach { measurementCardItem -> add(measurementCardItem) }
                add(SmallHeaderIItem(textRes = R.string.details))
                add(eventCardItem)
            }
        } else {
            title = string(R.string.event_missing)
            val missingEventCard = CardIItem {
                descRes = R.string.event_missing_desc
                buttonRes = R.string.preference_report_bug
                buttonClick = { SupportTopic.BUG.sendEmail(this@DetailActivity) }
            }
            adapter.add(missingEventCard)
        }

        recycler.adapter = adapter
    }

    private fun showInGoogleMaps() {
        if (isAppInstalled(GOOGLE_MAPS)) {
            if (isAppEnabled(GOOGLE_MAPS)) {
                val action: String = Intent.ACTION_VIEW

                val uriFormat: String = string(R.string.intent_uri_show_in_google_maps)
                val uriValues = arrayOf(
                    // English localization forces dot delimeter
                    String.format(Locale.ENGLISH, "%.5f", event.location.latitude),
                    String.format(Locale.ENGLISH, "%.5f", event.location.longitude),
                    event.title
                )
                val uri = Uri.parse(String.format(uriFormat, *uriValues))

                val navigationIntent = Intent(action, uri).apply { `package` = GOOGLE_MAPS }
                startActivity(navigationIntent)
            } else snackbarThemed(
                textRes = R.string.google_maps_not_enabled,
                builder = {
                    setAction(
                        R.string.snackbar_action_enable,
                        { showAppInfo(GOOGLE_MAPS) })
                }
            )
        } else snackbarThemed(
            textRes = R.string.google_maps_not_installed,
            builder = {
                setAction(
                    R.string.snackbar_action_install,
                    { startPlayStoreLink(GOOGLE_MAPS) })
            }
        )
    }

    companion object {
        const val EVENT_ID = "EVENT_ID"
        const val SHOW_MAP = "SHOW_MAP"
    }
}
