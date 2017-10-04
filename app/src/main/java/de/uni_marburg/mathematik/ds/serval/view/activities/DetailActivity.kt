package de.uni_marburg.mathematik.ds.serval.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.string
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.adapters.MeasurementsAdapter
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.util.MAP_ZOOM
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

/** Displays all details of an [event][Event]. */
class DetailActivity : AppCompatActivity(), OnMapReadyCallback, OnOffsetChangedListener {

    private lateinit var event: Event

    private lateinit var googleMap: GoogleMap

    private var isShown = true

    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        event = intent.extras.getParcelable(EVENT)
        setupViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupGoogleMap()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when (scrollRange) {
            -1 -> scrollRange = appBarLayout.totalScrollRange
            -verticalOffset -> collapsingToolbarLayout.title = string(R.string.details)
            else -> if (isShown) {
                collapsingToolbarLayout.title = " "
                isShown = false
            }
        }
    }

    private fun setupViews() {
        setupToolbar()
        setupRecyclerView()
        setupMap()
        fab.setOnClickListener { showInGoogleMaps() }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.title = string(R.string.details)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        appbar_layout.addOnOffsetChangedListener(this)
    }

    private fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler_view.adapter =
                MeasurementsAdapter(event.measurements) { measurement: Measurement, view: View ->
                    when (view.id) {
                        R.id.share -> shareText(measurement.toString())
                    }
                }
    }

    private fun setupMap() {
        val options = GoogleMapOptions().liteMode(true)
        val mapFragment = SupportMapFragment.newInstance(options)
        supportFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    private fun setupGoogleMap() {
        googleMap.uiSettings.setAllGesturesEnabled(false)
        googleMap.uiSettings.isMapToolbarEnabled = false
        addEventLocation(googleMap)
        // TODO Figure out how to disable Google Maps Intent on click
    }

    private fun addEventLocation(googleMap: GoogleMap) {
        val position = LatLng(event.location.latitude, event.location.longitude)
        googleMap.addMarker(MarkerOptions().position(position))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, MAP_ZOOM))
    }

    private fun showInGoogleMaps() {
        val navigationIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format(
                        string(R.string.intent_uri_show_in_google_maps),
                        // Forces decimal points
                        String.format(Locale.ENGLISH, "%.5f", event.location.latitude),
                        String.format(Locale.ENGLISH, "%.5f", event.location.longitude),
                        event.title
                ))
        )
        navigationIntent.`package` = "com.google.android.apps.maps"
        startActivity(navigationIntent)
    }

    companion object {

        const val EVENT = "EVENT"
    }
}
