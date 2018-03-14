package de.uni_marburg.mathematik.ds.serval.activities

import android.arch.lifecycle.ViewModelProviders
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
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.MapIItem
import de.uni_marburg.mathematik.ds.serval.views.SmallHeaderIItem
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Displays all details of an [event][Event].
 */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter: FastItemAdapter<IItem<*, *>> = FastItemAdapter()

    private lateinit var eventViewModel: EventViewModel
    private lateinit var event: Event
    private lateinit var geocodingControl: SmartLocation.GeocodingControl

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        geocodingControl = SmartLocation.with(this).geocoding()

        setSecureFlag()
        setColors {
            header(appBar)
            toolbar(toolbar)
            themeWindow = false
        }

        setup()

        setOutsideTapListener { finishAfterTransition() }

        return true
    }

    override fun onPause() {
        geocodingControl.stop()
        super.onPause()
    }

    private fun setup() {
        doAsync {
            val eventId: String = intent.extras.getString(EVENT_ID)
            event = eventViewModel[eventId]

            uiThread {
                title = event.title

                with(adapter) {
                    recycler.adapter = this
                    addGeneralCards()
                    addAddressCard()
//                    CardIItem.bindClickEvents(this)
                }

                with(fab) {
                    backgroundTintList = ColorStateList.valueOf(AppearancePrefs.Theme.accentColor)
                    setIcon(
                        icon = GoogleMaterial.Icon.gmd_navigation,
                        color = AppearancePrefs.Theme.iconColor
                    )
                    setOnClickListener { showInGoogleMaps() }
                    show()
                }
            }
        }
    }

    /**
     * Add general [event] detail cards.
     */
    private fun addGeneralCards() {
        val shouldShowMap: Boolean = intent.extras.getBoolean(SHOULD_SHOW_MAP)
        if (shouldShowMap) adapter.add(MapIItem(event))

        addMeasurementsCards()
        addDetailsCards()
    }

    /**
     * Add a card for each measurement of the [event].
     */
    private fun addMeasurementsCards() {
        val measurementCardItems = mutableListOf<CardIItem>()

        event.measurements.forEach { measurement ->
            val format = string(measurement.type.formatRes)
            val measurementDescription = String.format(format, measurement.value)
            val measurementCardItem = CardIItem {
                titleRes = measurement.type.titleRes
                desc = measurementDescription
                imageIIcon = measurement.type.iicon
                imageIIconColor = AppearancePrefs.Theme.iconColor
            }

            measurementCardItems.add(measurementCardItem)
        }

        val id = R.plurals.measurement
        val quantity = event.measurements.count()
        val measurementsHeader = SmallHeaderIItem(text = plural(id, quantity))

        with(adapter) {
            add(measurementsHeader)
            measurementCardItems.forEach { add(it) }
        }
    }

    /**
     * Add [event] details cards.
     */
    private fun addDetailsCards() {
        val detailsHeader = SmallHeaderIItem(textRes = R.string.event_details)

        val passedTime: String = event.passedSeconds.formatPassedSeconds(this)
        val timeCard = CardIItem {
            titleRes = R.string.event_time
            desc = "${event.snippet}\n$passedTime"
            imageIIcon = GoogleMaterial.Icon.gmd_access_time
        }

        with(adapter) {
            add(detailsHeader)
            doOnDebugBuild {
                val idCard = CardIItem {
                    titleRes = R.string.event_id
                    desc = event.id
                    imageIIcon = CommunityMaterial.Icon.cmd_account_card_details
                }
                add(idCard)
            }
            add(timeCard)
        }
    }

    /**
     * Add [event] address card.
     */
    private fun addAddressCard() {
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
    }

    /**
     * Show the [event's][event] location in Google Maps.
     */
    private fun showInGoogleMaps() {
        if (isAppInstalled(GOOGLE_MAPS)) {
            if (isAppEnabled(GOOGLE_MAPS)) {
                val action: String = Intent.ACTION_VIEW

                val latitude = event.latitude
                val longitude = event.longitude
                val title = event.title
                val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($title)")

                val mapIntent = Intent(action, uri).apply { `package` = GOOGLE_MAPS }
                if (mapIntent.resolveActivity(packageManager) != null) startActivity(mapIntent)
            } else {
                coordinator.snackbarThemed(
                    textRes = R.string.google_maps_not_enabled,
                    builder = {
                        setAction(R.string.snackbar_action_enable, { showAppInfo(GOOGLE_MAPS) })
                    }
                )
            }
        } else {
            coordinator.snackbarThemed(
                textRes = R.string.google_maps_not_installed,
                builder = {
                    setAction(R.string.snackbar_action_install, { startPlayStoreLink(GOOGLE_MAPS) })
                }
            )
        }
    }

    companion object {
        const val EVENT_ID = "EVENT_ID"
        const val SHOULD_SHOW_MAP = "SHOULD_SHOW_MAP"
    }

}
