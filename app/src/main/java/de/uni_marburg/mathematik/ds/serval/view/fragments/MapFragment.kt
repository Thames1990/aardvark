package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.hasPermission
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.util.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
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

    @SuppressLint("MissingPermission")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map.getMapAsync { googleMap ->
            this.googleMap = googleMap
            with(googleMap) {
                if (activity!!.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    isMyLocationEnabled = true
                }
                uiSettings.isMapToolbarEnabled = false
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
            val changeMapType = menu.findItem(R.id.action_change_map_type)
            val icon = context!!.drawable(R.drawable.map)
            icon.setColorFilter(context!!.color(android.R.color.white), PorterDuff.Mode.SRC_IN)
            changeMapType.icon = icon
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
            setAnimation(false)
            setOnCameraIdleListener(this)
            setOnMarkerClickListener(this)
            setOnInfoWindowClickListener(this)
            setOnClusterClickListener { cluster ->
                consume {
                    with(LatLngBounds.builder()) {
                        cluster.items.forEach { event -> include(event.position) }
                        cameraUpdate(build(), true)
                    }
                }
            }
            setOnClusterItemInfoWindowClickListener { event ->
                context!!.startActivity<DetailActivity>(DetailActivity.EVENT to event)
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
