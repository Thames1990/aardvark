package de.uni_marburg.mathematik.ds.serval.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
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
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MapFragment : BaseFragment() {

    private lateinit var clusterManager: ClusterManager<Event>
    private lateinit var eventViewModel: EventViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var map: SupportMapFragment

    override val layout: Int
        get() = R.layout.fragment_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO Set cluster manager items with view model
        eventViewModel = ViewModelProviders.of(requireActivity()).get(EventViewModel::class.java)

        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync { map ->
            googleMap = map.apply {
                isMyLocationEnabled = requireContext().hasLocationPermission
                style()
                setupClusterManager()
                zoomToAllMarkers(animate = false)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // Relocate camera to include all event markers on device orientation changes
        if (::googleMap.isInitialized) googleMap.zoomToAllMarkers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_map, menu)
        requireActivity().setMenuIcons(
            menu = menu,
            color = Prefs.iconColor,
            iicons = *arrayOf(
                R.id.action_zoom_to_all_markers to GoogleMaterial.Icon.gmd_zoom_out_map,
                R.id.action_change_map_type to GoogleMaterial.Icon.gmd_map
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = with(googleMap) {
        when (item.itemId) {
            R.id.action_change_map_type_hybrid -> mapType = MAP_TYPE_HYBRID
            R.id.action_change_map_type_none -> mapType = MAP_TYPE_NONE
            R.id.action_change_map_type_normal -> mapType = MAP_TYPE_NORMAL
            R.id.action_change_map_type_satellite -> mapType = MAP_TYPE_SATELLITE
            R.id.action_change_map_type_terrain -> mapType = MAP_TYPE_TERRAIN
            R.id.action_zoom_to_all_markers -> zoomToAllMarkers()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun GoogleMap.style() {
        setMapStyle(
            loadRawResourceStyle(
                context, when (Prefs.theme) {
                    Theme.AMOLED -> R.raw.map_style_night
                    Theme.LIGHT -> R.raw.map_style_standard
                    Theme.DARK -> R.raw.map_style_dark
                    Theme.CUSTOM -> Prefs.mapsStyle.style
                }
            )
        )
    }

    private fun GoogleMap.setupClusterManager() {
        val context = requireContext()

        clusterManager = ClusterManager<Event>(context, this).apply {
            setCallbacks(object : ClusterManager.Callbacks<Event> {
                override fun onClusterClick(cluster: Cluster<Event>): Boolean {
                    val builder = LatLngBounds.builder()
                    cluster.items.forEach { event ->
                        builder.include(event.position)
                        val bounds = builder.build()
                        cameraUpdate(bounds, Prefs.animate)
                    }
                    return true
                }

                override fun onClusterItemClick(event: Event): Boolean {
                    context.startActivity<DetailActivity>(
                        bundleBuilder = { if (Prefs.animate) withSceneTransitionAnimation(context) },
                        intentBuilder = { putExtra(DetailActivity.EVENT_ID, event.id) }
                    )
                    return true
                }

            })

            doAsync {
                val events: List<Event> = eventViewModel.getAll()
                uiThread { setItems(events) }
            }
        }

        setOnCameraIdleListener(clusterManager)
    }

    private fun GoogleMap.zoomToAllMarkers(animate: Boolean = Prefs.animate) {
        doAsync {
            val events: List<Event> = eventViewModel.getAll()
            // Check is necessary, because the cluster manager doesn't check for empty items
            if (events.isNotEmpty()) {
                val builder = LatLngBounds.builder()
                events.forEach { event -> builder.include(event.position) }
                uiThread {
                    val bounds: LatLngBounds = builder.build()
                    setLatLngBoundsForCameraTarget(bounds)
                    cameraUpdate(bounds, animate)
                }
            }
        }
    }

    private fun GoogleMap.cameraUpdate(bounds: LatLngBounds, animate: Boolean = Prefs.animate) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        if (animate) animateCamera(cameraUpdate) else moveCamera(cameraUpdate)
    }

    companion object {
        const val MAP_PADDING = 150
    }

}
