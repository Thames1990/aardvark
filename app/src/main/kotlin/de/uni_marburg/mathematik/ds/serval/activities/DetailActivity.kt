package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs
import de.uni_marburg.mathematik.ds.serval.utils.*
import io.nlopez.smartlocation.SmartLocation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Displays all details of an [event][Event].
 */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter: FastItemAdapter<IItem<*, *>> = FastItemAdapter()

    private lateinit var event: Event
    private lateinit var geocodingControl: SmartLocation.GeocodingControl
    private lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        eventViewModel = getViewModel()
        geocodingControl = SmartLocation.with(this).geocoding()

        setSecureFlag()
        setColors {
            header(appBar)
            toolbar(toolbar)
            themeWindow = false
        }

        setup()

        return true
    }

    override fun onPause() {
        geocodingControl.stop()
        super.onPause()
    }

    private fun setup() {
        coordinator.setMarginTop(0)

        doAsync {
            val eventId: String = intent.extras.getString(EVENT_ID)
            event = eventViewModel[eventId]

            uiThread {
                title = event.title

                recycler.adapter = adapter
                CardIItem.bindClickEvents(adapter)

                addGeneralCards()
                addAddressCard()

                fab.showWithOptions(
                    icon = CommunityMaterial.Icon.cmd_google_maps,
                    tooltipTextRes = R.string.tooltip_fab_show_in_google_maps,
                    backgroundColor = ColorStateList.valueOf(AppearancePrefs.Theme.accentColor),
                    onClickListener = ::showInGoogleMaps
                )
            }
        }

        setOutsideTapListener { finishAfterTransition() }
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
            val value: Double = measurement.conversionValue
            val unit: String = string(measurement.type.unit)

            val measurementCardItem = CardIItem {
                titleRes = measurement.type.titleRes
                desc = "$value $unit"
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
            add(*measurementCardItems.toTypedArray())
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

    private class MapIItem(val event: Event) : KauIItem<MapIItem, MapIItem.ViewHolder>(
        layoutRes = R.layout.iitem_map,
        viewHolder = ::ViewHolder,
        type = R.id.item_map
    ), ThemableIItem by ThemableIItemDelegate() {

        override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
            super.bindView(holder, payloads)
            with(holder.map) {
                onCreate(null)
                getMapAsync { googleMap ->
                    with(googleMap) {
                        val mapStyleRes: Int = when (AppearancePrefs.Theme.theme) {
                            Themes.AMOLED -> R.raw.map_style_night
                            Themes.LIGHT -> R.raw.map_style_standard
                            Themes.DARK -> R.raw.map_style_dark
                            Themes.CUSTOM -> MapPrefs.MapStyle.styleRes
                        }
                        setMapStyle(MapStyleOptions.loadRawResourceStyle(context, mapStyleRes))

                        with(uiSettings) {
                            isClickable = false
                            isMapToolbarEnabled = false
                            setAllGesturesEnabled(false)
                        }

                        val position: LatLng = event.position
                        val marker: MarkerOptions = MarkerOptions()
                            .position(position)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    IconicsDrawable(context)
                                        .icon(CommunityMaterial.Icon.cmd_map_marker)
                                        .color(AppearancePrefs.Theme.textColor)
                                        .toBitmap()
                                )
                            )
                        addMarker(marker)

                        val cameraUpdate =
                            CameraUpdateFactory.newLatLngZoom(position, MapPrefs.MAP_ZOOM)
                        moveCamera(cameraUpdate)
                    }
                }
            }
        }

        override fun unbindView(holder: ViewHolder) {
            super.unbindView(holder)
            holder.map.onDestroy()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val map: MapView by bindView(R.id.map)
        }
    }

    private class SmallHeaderIItem(
        text: String? = null,
        var textRes: Int = -1
    ) : KauIItem<SmallHeaderIItem, SmallHeaderIItem.ViewHolder>(
        layoutRes = R.layout.iitem_header,
        viewHolder = ::ViewHolder,
        type = R.id.item_small_header
    ), ThemableIItem by ThemableIItemDelegate() {

        override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
            super.bindView(holder, payloads)
            holder.text.setTextIfValid(textRes)
            bindTextColor(holder.text)
            bindBackgroundColor(holder.container)
        }

        override fun unbindView(holder: ViewHolder) {
            super.unbindView(holder)
            holder.text.text = null
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val text: TextView by bindView(R.id.kau_header_text)
            val container: CardView by bindView(R.id.kau_header_container)
        }

    }

}
