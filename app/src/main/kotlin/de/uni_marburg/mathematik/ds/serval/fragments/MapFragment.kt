package de.uni_marburg.mathematik.ds.serval.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.withSceneTransitionAnimation
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs.MAP_ZOOM
import de.uni_marburg.mathematik.ds.serval.utils.*
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import net.sharewire.googlemapsclustering.IconGenerator

class MapFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_map

    private lateinit var clusterManager: ClusterManager<Event>
    private lateinit var events: List<Event>
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync {
            googleMap = it

            setupClusterManager()
            setupGoogleMap()

            fun submitEvents(events: List<Event>?) {
                if (events != null) {
                    this.events = events
                    clusterManager.setItems(events)
                    zoomToAllMarkers(animate = false)
                }
            }

            observe(liveData = eventViewModel.liveData, onChanged = ::submitEvents)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) = createOptionsMenu(
        inflater = inflater,
        menuRes = R.menu.menu_map,
        menu = menu,
        iicons = *arrayOf(R.id.action_change_map_type to GoogleMaterial.Icon.gmd_layers)
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        googleMap.mapType = when (item.itemId) {
            R.id.action_change_map_type_normal -> GoogleMap.MAP_TYPE_NORMAL
            R.id.action_change_map_type_satellite -> GoogleMap.MAP_TYPE_SATELLITE
            R.id.action_change_map_type_terrain -> GoogleMap.MAP_TYPE_TERRAIN
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun moveToPosition(position: LatLng, animate: Boolean = animationsAreEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

    fun zoomToAllMarkers(animate: Boolean = animationsAreEnabled) {
        if (events.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            events.forEach { event -> builder.include(event.position) }
            val bounds: LatLngBounds = builder.build()
            moveToBounds(bounds, animate)
        }
    }

    private fun setupClusterManager() {
        val context = requireContext()

        fun ClusterManager<Event>.setCallbacks() {
            setCallbacks(object : ClusterManager.Callbacks<Event> {
                override fun onClusterClick(cluster: Cluster<Event>): Boolean {
                    val builder = LatLngBounds.builder()
                    cluster.items.forEach { event -> builder.include(event.position) }
                    val bounds = builder.build()
                    moveToBounds(bounds)
                    return true
                }

                override fun onClusterItemClick(event: Event): Boolean {
                    context.startActivity<DetailActivity>(
                        bundleBuilder = {
                            if (animationsAreEnabled) withSceneTransitionAnimation(context)
                        },
                        intentBuilder = { putExtra(DetailActivity.EVENT_ID, event.id) }
                    )
                    return true
                }

            })
        }

        fun ClusterManager<Event>.setIcons() {
            setIconGenerator(object : IconGenerator<Event> {
                override fun getClusterIcon(cluster: Cluster<Event>): BitmapDescriptor {
                    val eventCount: Int = cluster.items.size
                    val iconText: String =
                        if (MapPrefs.showExactClusterSize) eventCount.toString()
                        else when (eventCount) {
                            in 0..10 -> eventCount.toString()
                            in 10..50 -> "10+"
                            in 50..100 -> "50+"
                            in 100..250 -> "100+"
                            in 500..1000 -> "500+"
                            else -> "1000+"
                        }

                    return BitmapDescriptorFactory.fromBitmap(
                        IconicsDrawable(context)
                            .iconText(iconText)
                            .color(AppearancePrefs.Theme.textColor)
                            .backgroundColor(AppearancePrefs.Theme.backgroundColor)
                            .backgroundContourColor(AppearancePrefs.Theme.textColor)
                            .backgroundContourWidthDp(CLUSTER_ICON_BACKGROUND_CONTOUR_WIDTH)
                            .sizeDp(CLUSTER_ICON_SIZE)
                            .roundedCornersDp(CLUSTER_ICON_SIZE / 2)
                            .paddingDp(CLUSTER_ICON_PADDING)
                            .toBitmap()
                    )
                }

                override fun getClusterItemIcon(
                    event: Event
                ): BitmapDescriptor = styledMarker(context)

            })
        }

        clusterManager = ClusterManager<Event>(context, googleMap).apply {
            setCallbacks()
            setIcons()
        }

        googleMap.setOnCameraIdleListener(clusterManager)
    }

    @SuppressLint("MissingPermission")
    private fun setupGoogleMap() = with(googleMap) {
        isMyLocationEnabled = requireContext().hasLocationPermission

        withStyle(requireContext())

        with(uiSettings) {
            isCompassEnabled = MapPrefs.compassEnabled
            isIndoorLevelPickerEnabled = MapPrefs.indoorLevelPickerEnabled
            isMyLocationButtonEnabled = false
            isRotateGesturesEnabled = MapPrefs.Gestures.rotateEnabled
            isScrollGesturesEnabled = MapPrefs.Gestures.scrollEnabled
            isTiltGesturesEnabled = MapPrefs.Gestures.tiltEnabled
            isZoomGesturesEnabled = MapPrefs.Gestures.zoomEnabled
        }

        isBuildingsEnabled = MapPrefs.Layers.buildingsEnabled
        isIndoorEnabled = MapPrefs.Layers.indoorEnabled
        isTrafficEnabled = MapPrefs.Layers.trafficEnabled
    }

    private fun moveToBounds(bounds: LatLngBounds, animate: Boolean = animationsAreEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

    companion object {
        const val CLUSTER_ICON_BACKGROUND_CONTOUR_WIDTH = 1
        const val CLUSTER_ICON_PADDING = 8
        const val CLUSTER_ICON_SIZE = 42
    }

}
