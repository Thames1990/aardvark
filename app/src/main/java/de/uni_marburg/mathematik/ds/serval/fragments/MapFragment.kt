package de.uni_marburg.mathematik.ds.serval.fragments

import android.annotation.SuppressLint
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
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import net.sharewire.googlemapsclustering.Cluster
import net.sharewire.googlemapsclustering.ClusterManager
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MapFragment : BaseFragment() {

    // TODO Use viewmodel to set cluster manager items
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

        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)

        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync { map ->
            googleMap = map.apply {
                isMyLocationEnabled = context!!.hasLocationPermission
                style()
                setupClusterManager()
                zoomToAllMarkers(animate = false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_map, menu)
        activity?.setMenuIcons(
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
        when (Prefs.theme) {
            Theme.DARK -> setMapStyle(loadRawResourceStyle(context, R.raw.map_style_dark))
            Theme.AMOLED -> setMapStyle(
                loadRawResourceStyle(
                    context,
                    R.raw.map_style_night
                )
            )
            Theme.LIGHT -> Unit
            Theme.CUSTOM -> Unit
        }
    }

    private fun GoogleMap.setupClusterManager() {
        context?.let { context ->
            val clusterManger = ClusterManager<Event>(context, this).apply {
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
                            bundleBuilder = {
                                if (Prefs.animate) withSceneTransitionAnimation(context)
                            },
                            intentBuilder = {
                                putExtra(DetailActivity.EVENT_ID, event.id)
                                putExtra(DetailActivity.SHOW_MAP, false)
                            }
                        )
                        return true
                    }
                })

                doAsync {
                    val events: List<Event> = eventViewModel.getAll()
                    uiThread { setItems(events) }
                }
            }
            setOnCameraIdleListener(clusterManger)
        }
    }

    private fun GoogleMap.zoomToAllMarkers(animate: Boolean = Prefs.animate) {
        doAsync {
            val events: List<Event> = eventViewModel.getAll()
            if (events.isNotEmpty()) {
                uiThread { googleMap ->
                    if (events.isNotEmpty()) {
                        val builder = LatLngBounds.builder()
                        doAsync {
                            events.forEach { event -> builder.include(event.position) }
                            uiThread {
                                val bounds: LatLngBounds = builder.build()
                                googleMap.setLatLngBoundsForCameraTarget(bounds)
                                googleMap.cameraUpdate(bounds, animate)
                            }
                        }
                    }
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
