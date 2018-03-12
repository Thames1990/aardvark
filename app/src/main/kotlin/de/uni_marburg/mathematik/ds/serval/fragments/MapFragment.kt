package de.uni_marburg.mathematik.ds.serval.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.setMenuIcons
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.withSceneTransitionAnimation
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.BehaviourPrefs
import de.uni_marburg.mathematik.ds.serval.settings.MapPrefs
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager

class MapFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_map

    private val viewModel: EventViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(EventViewModel::class.java)
    }

    private lateinit var googleMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<Event>
    private lateinit var map: SupportMapFragment

    private lateinit var events: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync { map ->
            googleMap = map.apply { isMyLocationEnabled = requireContext().hasLocationPermission }

            setupClusterManager()
            setupGoogleMap()

            viewModel.eventLiveData.observe(requireActivity(), Observer<List<Event>> { events ->
                if (events != null) {
                    this.events = events
                    clusterManager.setItems(events)
                    zoomToAllMarkers(animate = false)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_map, menu)
        requireActivity().setMenuIcons(
            menu = menu,
            color = AppearancePrefs.Theme.iconColor,
            iicons = *arrayOf(R.id.action_change_map_type to GoogleMaterial.Icon.gmd_layers)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        googleMap.mapType = when (item.itemId) {
            R.id.action_change_map_type_normal -> GoogleMap.MAP_TYPE_NORMAL
            R.id.action_change_map_type_satellite -> GoogleMap.MAP_TYPE_SATELLITE
            R.id.action_change_map_type_terrain -> GoogleMap.MAP_TYPE_TERRAIN
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun moveToPosition(position: LatLng, animate: Boolean = BehaviourPrefs.animationsEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLng(position)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

    fun zoomToAllMarkers(animate: Boolean = BehaviourPrefs.animationsEnabled) {
        if (events.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            events.forEach { event -> builder.include(event.position) }
            val bounds: LatLngBounds = builder.build()
            with(googleMap) {
                setLatLngBoundsForCameraTarget(bounds)
                moveToBounds(bounds, animate)
            }
        }
    }

    private fun setupClusterManager() {
        val context = requireContext()

        clusterManager = ClusterManager<Event>(context, googleMap).apply {
            setCallbacks(object : ClusterManager.Callbacks<Event> {
                override fun onClusterClick(cluster: Cluster<Event>): Boolean {
                    val builder = LatLngBounds.builder()
                    cluster.items.forEach { event ->
                        builder.include(event.position)
                        val bounds = builder.build()
                        moveToBounds(bounds)
                    }
                    return true
                }

                override fun onClusterItemClick(event: Event): Boolean {
                    context.startActivity<DetailActivity>(
                        bundleBuilder = {
                            if (BehaviourPrefs.animationsEnabled) withSceneTransitionAnimation(context)
                        },
                        intentBuilder = { putExtra(DetailActivity.EVENT_ID, event.id) }
                    )
                    return true
                }

            })
        }

        googleMap.setOnCameraIdleListener(clusterManager)
    }

    private fun setupGoogleMap() = with(googleMap) {
        val rawResourceRes: Int = when (AppearancePrefs.Theme.theme) {
            Themes.AMOLED -> R.raw.map_style_night
            Themes.LIGHT -> R.raw.map_style_standard
            Themes.DARK -> R.raw.map_style_dark
            Themes.CUSTOM -> MapPrefs.MapStyle.styleRes
        }
        setMapStyle(MapStyleOptions.loadRawResourceStyle(context, rawResourceRes))

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

    private fun moveToBounds(bounds: LatLngBounds, animate: Boolean = BehaviourPrefs.animationsEnabled) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        if (animate) googleMap.animateCamera(cameraUpdate)
        else googleMap.moveCamera(cameraUpdate)
    }

    companion object {
        const val MAP_PADDING = 150
    }

}
