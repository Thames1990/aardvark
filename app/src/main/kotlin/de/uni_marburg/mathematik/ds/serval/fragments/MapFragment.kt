package de.uni_marburg.mathematik.ds.serval.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.restart
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.withSceneTransitionAnimation
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs.MAP_ZOOM
import de.uni_marburg.mathematik.ds.serval.utils.*
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager

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

            observe(liveData = eventViewModel.events, onChanged = ::submitEvents)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) = createOptionsMenu(
        menu = menu,
        inflater = inflater,
        menuRes = R.menu.menu_map,
        iicons = *arrayOf(R.id.action_map_type to GoogleMaterial.Icon.gmd_layers)
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        googleMap.mapType = when (item.itemId) {
            R.id.action_map_type_normal -> GoogleMap.MAP_TYPE_NORMAL
            R.id.action_map_type_satellite -> GoogleMap.MAP_TYPE_SATELLITE
            R.id.action_map_type_terrain -> GoogleMap.MAP_TYPE_TERRAIN
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSelected(
        appBarLayout: AppBarLayout,
        toolbar: Toolbar,
        fab: FloatingActionButton
    ) {
        appBarLayout.expand()
        with(toolbar) {
            updateLayoutParams<AppBarLayout.LayoutParams> { scrollFlags = 0 }
            title = context.string(R.string.tab_item_map)
        }
        fab.showWithOptions(
            icon = GoogleMaterial.Icon.gmd_my_location,
            tooltipTextRes = R.string.tooltip_fab_move_to_current_location,
            onClickListener = {
                val context: Context = appBarLayout.context
                with(context) {
                    if (!hasLocationPermission) {
                        kauRequestPermissions(PERMISSION_ACCESS_FINE_LOCATION) { granted, _ ->
                            if (granted) activity?.restart()
                            else fab.snackbarThemed(R.string.preference_location_requires_location_permission)
                        }
                    } else moveToPosition(MainActivity.devicePosition)
                }
                appBarLayout.expand()
            },
            show = MapPrefs.myLocationButtonEnabled
        )
    }

    override fun onReselected() = zoomToAllMarkers()

    private fun moveToPosition(position: LatLng, animate: Boolean = animationsAreEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

    private fun zoomToAllMarkers(animate: Boolean = animationsAreEnabled) {
        if (events.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            events.forEach { event -> builder.include(event.position) }
            val bounds: LatLngBounds = builder.build()
            moveToBounds(bounds, animate)
        }
    }

    private fun setupClusterManager() {
        val context = requireContext()
        clusterManager = ClusterManager<Event>(context, googleMap).apply {
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
            setIcons(context)
        }

        googleMap.setOnCameraIdleListener(clusterManager)
    }

    @SuppressLint("MissingPermission")
    private fun setupGoogleMap() = with(googleMap) {
        isMyLocationEnabled = requireContext().hasLocationPermission &&
                MapPrefs.myLocationButtonEnabled

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

        doOnDebugBuild {
            setOnMyLocationClickListener { location ->
                view?.snackbarThemed("lat: ${location.latitude}, lon: ${location.longitude}")
            }
        }
    }

    private fun moveToBounds(bounds: LatLngBounds, animate: Boolean = animationsAreEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

}
