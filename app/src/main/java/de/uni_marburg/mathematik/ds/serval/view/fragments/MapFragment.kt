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
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.util.CHECK_LOCATION_PERMISSION
import de.uni_marburg.mathematik.ds.serval.util.MAP_PADDING
import de.uni_marburg.mathematik.ds.serval.util.MAP_ZOOM
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity

class MapFragment :
        BaseFragment(),
        OnClusterItemInfoWindowClickListener<Event>,
        OnClusterClickListener<Event>,
        OnMapReadyCallback,
        OnMyLocationButtonClickListener {

    private lateinit var clusterManager: ClusterManager<Event>

    private lateinit var googleMap: GoogleMap

    private lateinit var map: SupportMapFragment

    override val layout: Int
        get() = R.layout.fragment_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map.getMapAsync(this)
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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupGoogleMap()
        if (Preferences.trackLocation) {
            moveCameraToLastLocation(false)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        moveCameraToLastLocation()
        return true
    }

    override fun onClusterClick(cluster: Cluster<Event>): Boolean {
        val builder = LatLngBounds.builder()
        for (clusterItem in cluster.items) {
            builder.include(clusterItem.position)
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING))
        return true
    }

    override fun onClusterItemInfoWindowClick(event: Event) {
        val eventIntent = Intent(activity, DetailActivity::class.java)
        eventIntent.putExtra(DetailActivity.EVENT, event)
        startActivity(eventIntent)
    }

    private fun setupGoogleMap() {
        setupClusterManager()
        setupGoogleMapsListeners(clusterManager)
        setupCameraBounds()
        setupMyLocation()
    }

    private fun setupClusterManager() {
        clusterManager = ClusterManager(context, googleMap)
        clusterManager.setOnClusterClickListener(this)
        clusterManager.setOnClusterItemInfoWindowClickListener(this)
        clusterManager.addItems(MainActivity.events)
        clusterManager.cluster()
    }

    private fun setupGoogleMapsListeners(clusterManager: ClusterManager<Event>) {
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        googleMap.setOnInfoWindowClickListener(clusterManager)
    }

    private fun setupCameraBounds() {
        if (!MainActivity.events.isEmpty()) {
            val builder = LatLngBounds.builder()
            MainActivity.events.forEach { event: Event -> builder.include(event.position) }
            googleMap.setLatLngBoundsForCameraTarget(builder.build())
        }
    }

    private fun setupMyLocation() {
        if (Preferences.trackLocation) {
            if (!context.hasPermission(ACCESS_FINE_LOCATION)) {
                requestPermissions(
                        activity,
                        arrayOf(ACCESS_FINE_LOCATION),
                        CHECK_LOCATION_PERMISSION
                )
                return
            }
            googleMap.isMyLocationEnabled = true
            googleMap.setOnMyLocationButtonClickListener(this)
        }
    }

    private fun moveCameraToLastLocation(animate: Boolean = true) {
        val lastLocationPosition = LatLng(
                MainActivity.lastLocation!!.latitude,
                MainActivity.lastLocation!!.longitude
        )
        val update = CameraUpdateFactory.newLatLngZoom(lastLocationPosition, MAP_ZOOM)
        if (animate) {
            googleMap.animateCamera(update)
        } else {
            googleMap.moveCamera(update)
        }
    }
}
