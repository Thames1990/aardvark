package de.uni_marburg.mathematik.ds.serval.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.hasPermission
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.utils.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.consume
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class MapFragment : BaseFragment() {

    private lateinit var googleMap: GoogleMap

    private val map: SupportMapFragment by lazy {
        childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
                if (hasLocationPermission) isMyLocationEnabled = true
                style()
                setupClusterManager()
                setupCamera()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_map, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.let {
            super.onPrepareOptionsMenu(menu)
            with(context!!) {
                val changeMapType = menu.findItem(R.id.action_change_map_type)
                val icon = drawable(R.drawable.map)
                icon.setColorFilter(Prefs.iconColor, PorterDuff.Mode.SRC_IN)
                changeMapType.icon = icon
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = with(googleMap) {
        when (item.itemId) {
            R.id.action_change_map_type_hybrid -> consume { mapType = MAP_TYPE_HYBRID }
            R.id.action_change_map_type_none -> consume { mapType = MAP_TYPE_NONE }
            R.id.action_change_map_type_normal -> consume { mapType = MAP_TYPE_NORMAL }
            R.id.action_change_map_type_satellite -> consume { mapType = MAP_TYPE_SATELLITE }
            R.id.action_change_map_type_terrain -> consume { mapType = MAP_TYPE_TERRAIN }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun GoogleMap.style() {
        uiSettings.isMapToolbarEnabled = false
        when (Prefs.theme) {
            Theme.DARK.ordinal -> setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
            )
            Theme.AMOLED.ordinal -> setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_night)
            )
        }
    }

    private fun GoogleMap.setupCamera() {
        if (MainActivity.events.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            doAsync {
                MainActivity.events.forEach { builder.include(it.position) }
                uiThread {
                    with(builder.build()) {
                        googleMap.setLatLngBoundsForCameraTarget(this)
                        googleMap.cameraUpdate(this)
                    }
                }
            }
        }
    }

    private fun GoogleMap.setupClusterManager() {
        with(ClusterManager<Event>(context, this)) {
            setAnimation(Prefs.animate)
            setOnCameraIdleListener(this)
            setOnMarkerClickListener(this)
            setOnInfoWindowClickListener(this)
            setOnClusterClickListener { cluster ->
                consume {
                    with(LatLngBounds.builder()) {
                        cluster.items.forEach { event -> include(event.position) }
                        cameraUpdate(build(), Prefs.animate)
                    }
                }
            }
            setOnClusterItemInfoWindowClickListener { event ->
                context!!.startActivity<DetailActivity>(
                        DetailActivity.EVENT to event,
                        DetailActivity.SHOW_MAP to false
                )
            }

            doAsync {
                with(MainActivity.events) {
                    addItems(this)
                    uiThread { cluster() }
                }
            }
        }
    }

    private fun GoogleMap.cameraUpdate(bounds: LatLngBounds, animate: Boolean = false) {
        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
        if (animate) animateCamera(cameraUpdate) else moveCamera(cameraUpdate)
    }

}
