package de.uni_marburg.mathematik.ds.serval.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.setMenuIcons
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.google.maps.android.clustering.ClusterManager
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class MapFragment : BaseFragment() {

    private lateinit var googleMap: GoogleMap

    private val map: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    private val eventViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override val layout: Int
        get() = R.layout.fragment_map

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map.getMapAsync { googleMap ->
            this.googleMap = googleMap
            with(googleMap) {
                val hasLocationPermission = context!!.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                isMyLocationEnabled = hasLocationPermission
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
        uiSettings.isScrollGesturesEnabled = false
        uiSettings.isMapToolbarEnabled = false
        setMinZoomPreference(MAP_MIN_ZOOM)
        when (Prefs.theme) {
            Theme.DARK.ordinal -> setMapStyle(loadRawResourceStyle(context, R.raw.map_style_dark))
            Theme.AMOLED.ordinal -> setMapStyle(loadRawResourceStyle(context, R.raw.map_style_night))
        }
    }

    private fun GoogleMap.setupClusterManager() {
        with(ClusterManager<Event>(context, this)) {
            setAnimation(Prefs.animate)
            setOnCameraIdleListener(this)
            setOnMarkerClickListener(this)
            setOnInfoWindowClickListener(this)
            setOnClusterClickListener {
                with(LatLngBounds.builder()) {
                    it.items.forEach { event -> include(event.position) }
                    cameraUpdate(build(), Prefs.animate)
                }
                true
            }
            setOnClusterItemInfoWindowClickListener {
                context!!.startActivity<DetailActivity>(
                        params = *arrayOf(
                                DetailActivity.EVENT_ID to it.id,
                                DetailActivity.SHOW_MAP to false
                        )
                )
            }

            doAsync {
                with(eventViewModel.dao.getAll()) {
                    addItems(this)
                    uiThread { cluster() }
                }
            }
        }
    }

    private fun GoogleMap.zoomToAllMarkers(animate: Boolean = Prefs.animate) {
        doAsync {
            val events: List<Event> = eventViewModel.dao.getAll()
            uiThread {
                if (events.isNotEmpty()) {
                    val builder = LatLngBounds.builder()
                    doAsync {
                        events.forEach { builder.include(it.position) }
                        uiThread {
                            with(builder.build()) {
                                googleMap.setLatLngBoundsForCameraTarget(this)
                                googleMap.cameraUpdate(bounds = this, animate = animate)
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
        const val MAP_MIN_ZOOM = 5f
        const val MAP_PADDING = 150
    }

}
