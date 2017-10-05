package de.uni_marburg.mathematik.ds.serval.view.activities

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventProvider
import de.uni_marburg.mathematik.ds.serval.util.CHECK_LOCATION_PERMISSION
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.changelog_bottom_sheet_dialog.view.*
import ru.noties.markwon.Markwon
import java.io.BufferedReader
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main view of the app.
 *
 *
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        events = EventProvider.load() ?: emptyList()
        setupLocationUpdate()
        setupViews()
        checkForNewVersion()
    }

    override fun onPause() {
        super.onPause()
        if (Preferences.trackLocation) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasPermission(ACCESS_FINE_LOCATION)) {
            Preferences.trackLocation = true
            startLocationUpdates()
        } else {
            Preferences.trackLocation = false
        }
    }

    override fun onBackPressed() {
        if (Preferences.confirmExit) {
            materialDialog {
                title(R.string.confirm_exit)
                positiveText(R.string.exit)
                onPositive({ _: MaterialDialog, _: DialogAction -> finishSlideOut() })
            }
        } else {
            finishSlideOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_changelog -> checkForNewVersion(true)
            R.id.action_settings -> startActivity(SettingsActivity::class.java)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            CHECK_LOCATION_PERMISSION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    Preferences.trackLocation = true
                    startLocationUpdates()
                } else {
                    Preferences.trackLocation = false
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setupLocationUpdate() {
        if (Preferences.trackLocation) {
            fusedLocationProviderClient = FusedLocationProviderClient(this)
            locationRequest = LocationRequest()
            with(locationRequest) {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(5)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    val location = locationResult!!.lastLocation
                    if (LocationUtil.isBetterLocation(location, lastLocation)) {
                        lastLocation = location
                    }
                }
            }
        }
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        changeScreen(PlaceholderFragment())
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            val current = supportFragmentManager.findFragmentById(R.id.content)
            val fragment = when (item.itemId) {
                R.id.action_dashboard -> PlaceholderFragment().takeIf { current !is DashboardFragment }
                R.id.action_events -> EventsFragment().takeIf { current !is EventsFragment }
                R.id.action_map -> MapFragment().takeIf { current !is MapFragment }
                else -> PlaceholderFragment()
            }
            if (fragment != null) {
                changeScreen(fragment)
            }
            false
        }
    }

    private fun changeScreen(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }

    private fun checkForNewVersion(force: Boolean = false) {
        val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
        if (force || Preferences.showChangelog && Preferences.lastKnownVersionCode < versionCode) {
            Preferences.lastKnownVersionCode = versionCode
            showChangelog(versionCode)
        }
    }

    private fun showChangelog(versionCode: Int) {
        val versionName = String.format(
                Locale.getDefault(),
                string(R.string.changelog),
                BuildConfig.VERSION_NAME
        )
        val changelog = assets.open(String.format(
                string(R.string.file_changelog),
                versionCode
        )).use { input -> input.bufferedReader().use(BufferedReader::readText) }
        if (Preferences.useBottomSheetDialogs) {
            showChangelogBottomSheetDialog(versionName, changelog)
        } else {
            showChangelogDialog(versionName, changelog)
        }
    }

    @SuppressLint("InflateParams")
    private fun showChangelogBottomSheetDialog(versionName: String, changelog: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.changelog_bottom_sheet_dialog, null)
        view.version.text = versionName
        Markwon.setMarkdown(view.changelog, changelog)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showChangelogDialog(versionName: String, changelog: String) {
        val content = TextView(this)
        Markwon.setMarkdown(content, changelog)
        materialDialog {
            title(versionName)
            customView(content, true)
            positiveText(android.R.string.ok)
        }
    }

    private fun startLocationUpdates() {
        if (!hasPermission(ACCESS_FINE_LOCATION)) {
            requestPermissions()
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
        )
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION),
                CHECK_LOCATION_PERMISSION
        )
    }

    companion object {

        var lastLocation: Location? = null

        lateinit var events: List<Event>
    }
}
