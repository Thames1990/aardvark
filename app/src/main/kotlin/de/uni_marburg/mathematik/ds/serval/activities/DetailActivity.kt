package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import ca.allanwang.kau.adapters.FastItemThemedAdapter
import ca.allanwang.kau.adapters.ThemableIItem
import ca.allanwang.kau.adapters.ThemableIItemDelegate
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.fastadapter.IItem
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs
import de.uni_marburg.mathematik.ds.serval.utils.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Displays all details of an [event][Event].
 */
class DetailActivity : ElasticRecyclerActivity() {

    private lateinit var adapter: FastItemThemedAdapter<IItem<*, *>>
    private lateinit var event: Event
    private lateinit var eventViewModel: EventViewModel
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        eventViewModel = getViewModel()
        setColors {
            header(appBar)
            toolbar(toolbar)
            themeWindow = false
        }
        setup()
        return true
    }

    private fun setup() {
        geocoder = Geocoder(this)
        adapter = FastItemThemedAdapter(
            textColor = AppearancePrefs.Theme.textColor,
            backgroundColor = AppearancePrefs.Theme.backgroundColor,
            accentColor = AppearancePrefs.Theme.accentColor
        )
        recycler.adapter = adapter

        coordinator.setMarginTop(0)
        CardIItem.bindClickEvents(adapter)
        setOutsideTapListener { finishAfterTransition() }

        doAsync {
            val eventId: String = intent.extras.getString(EVENT_ID)
            event = eventViewModel[eventId]

            uiThread {
                title = event.title

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
            val value: Double = measurement.convertedValue
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
        val measurementsHeader = SmallHeaderIItem(plural(id, quantity))

        with(adapter) {
            add(measurementsHeader)
            add(measurementCardItems)
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
        doAsync {
            val addresses: List<Address> = geocoder.getFromLocation(
                event.latitude,
                event.longitude,
                1
            )
            uiThread {
                if (addresses.isNotEmpty()) {
                    val addressCard = CardIItem {
                        titleRes = R.string.location_address
                        desc = addresses.mostProbableAddressLine
                        imageIIcon = CommunityMaterial.Icon.cmd_map_marker
                    }
                    adapter.add(addressCard)
                }
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
                        withAction(
                            titleRes = R.string.snackbar_action_enable,
                            onClick = { showAppInfo(GOOGLE_MAPS) })
                    }
                )
            }
        } else {
            coordinator.snackbarThemed(
                textRes = R.string.google_maps_not_installed,
                builder = {
                    withAction(
                        titleRes = R.string.snackbar_action_install,
                        onClick = { startPlayStoreLink(GOOGLE_MAPS) }
                    )
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
                        withStyle(context)

                        with(uiSettings) {
                            isClickable = false
                            isMapToolbarEnabled = false
                            setAllGesturesEnabled(false)
                        }

                        val position: LatLng = event.position
                        val marker: MarkerOptions = MarkerOptions()
                            .position(position)
                            .icon(styledMarker(context))
                        addMarker(marker)

                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            position,
                            MapPrefs.MAP_ZOOM
                        )
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

        var text: String = text ?: "Header Placeholder"

        override fun bindView(holder: ViewHolder, payloads: MutableList<Any>) {
            super.bindView(holder, payloads)
            holder.text.text = holder.itemView.context.string(textRes, text)
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
