package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.hasPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.util.CHECK_LOCATION_PERMISSION
import de.uni_marburg.mathematik.ds.serval.util.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.util.MAP_ZOOM
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity

class MapFragment : BaseFragment() {

    private lateinit var googleMap: GoogleMap

    private lateinit var map: SupportMapFragment

    override val layout: Int
        get() = R.layout.fragment_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Aardvark.firebaseAnalytics.setCurrentScreen(activity, getString(R.string.screen_map), null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map.getMapAsync { googleMap ->
            this.googleMap = googleMap
            val clusterManager = setupClusterManager(googleMap)
            setupGoogleMapsListeners(clusterManager, googleMap)
            setupCameraBounds(googleMap)
            setupMyLocation(googleMap)
            if (Preferences.trackLocation) {
                moveCameraToLastLocation(googleMap)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val changeMapType = menu!!.findItem(R.id.action_change_map_type)
        val icon = context.drawable(R.drawable.map)
        icon.setColorFilter(context.color(android.R.color.white), PorterDuff.Mode.SRC_IN)
        changeMapType.icon = icon
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_change_map_type_hybrid -> googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.action_change_map_type_none -> googleMap.mapType = GoogleMap.MAP_TYPE_NONE
            R.id.action_change_map_type_normal -> googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.action_change_map_type_satellite -> googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.action_change_map_type_terrain -> googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupClusterManager(googleMap: GoogleMap): ClusterManager<Event> {
        val clusterManager: ClusterManager<Event> = ClusterManager(context, googleMap)
        with(clusterManager) {
            setOnClusterClickListener { cluster ->
                val builder = LatLngBounds.builder()
                for (clusterItem in cluster.items) {
                    builder.include(clusterItem.position)
                }
                val bounds = builder.build()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING))
                true
            }
            setOnClusterItemInfoWindowClickListener { event ->
                val eventIntent = Intent(activity, DetailActivity::class.java)
                eventIntent.putExtra(DetailActivity.EVENT, event)
                startActivity(eventIntent)
            }
            addItems(MainActivity.events)
            cluster()
        }
        return clusterManager
    }

    private fun setupGoogleMapsListeners(
            clusterManager: ClusterManager<Event>,
            googleMap: GoogleMap
    ) {
        with(googleMap) {
            uiSettings.isMapToolbarEnabled = false
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
            setOnInfoWindowClickListener(clusterManager)
        }
    }

    private fun setupCameraBounds(googleMap: GoogleMap) {
        if (!MainActivity.events.isEmpty()) {
            val builder = LatLngBounds.builder()
            MainActivity.events.forEach { event: Event -> builder.include(event.position) }
            googleMap.setLatLngBoundsForCameraTarget(builder.build())
        }
    }

    private fun setupMyLocation(googleMap: GoogleMap) {
        if (Preferences.trackLocation) {
            if (!context.hasPermission(ACCESS_FINE_LOCATION)) {
                requestPermissions(
                        activity,
                        arrayOf(ACCESS_FINE_LOCATION),
                        CHECK_LOCATION_PERMISSION
                )
                return
            }
            with(googleMap) {
                isMyLocationEnabled = true
                setOnMyLocationButtonClickListener {
                    moveCameraToLastLocation(googleMap, true)
                    true
                }
            }
        }
    }

    private fun moveCameraToLastLocation(googleMap: GoogleMap, animate: Boolean = false) {
        with(MainActivity.lastLocation!!) {
            val lastLocationPosition = LatLng(latitude, longitude)
            val update = CameraUpdateFactory.newLatLngZoom(lastLocationPosition, MAP_ZOOM)
            if (animate) {
                googleMap.animateCamera(update)
            } else {
                googleMap.moveCamera(update)
            }
        }
    }
}
