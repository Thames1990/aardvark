package de.uni_marburg.mathematik.ds.serval.view.activities

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import ca.allanwang.kau.utils.finishSlideOut
import ca.allanwang.kau.utils.string
import com.crashlytics.android.Crashlytics
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.changelog_bottom_sheet_dialog.view.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import ru.noties.markwon.Markwon
import java.io.BufferedReader
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main view of the app.
 *
 *
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
class MainActivity :
        AppCompatActivity(),
        BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var client: OkHttpClient

    private lateinit var request: Request

    private lateinit var moshi: Moshi

    private lateinit var eventAdapter: JsonAdapter<Event>

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFields()
        loadEvents()
        setupLocationUpdate()
    }

    override fun onPause() {
        super.onPause()
        if (Preferences.trackLocation) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
        Preferences.bottomNavigationSelectedItemId = bottom_navigation.selectedItemId
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            Preferences.trackLocation = true
            startLocationUpdates()
        } else {
            Preferences.trackLocation = false
        }
    }

    override fun onBackPressed() {
        if (Preferences.confirmExit) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_exit)
                    .setPositiveButton(R.string.exit, { _: DialogInterface, _: Int ->
                        finishSlideOut()
                    })
                    .create()
                    .show()
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
            R.id.action_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment: Fragment? = null
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content)

        when (item.itemId) {
            R.id.action_dashboard -> if (currentFragment !is DashboardFragment) {
                fragment = PlaceholderFragment()
                Aardvark.firebaseAnalytics.setCurrentScreen(this, string(R.string.screen_dashboard), null)
            }
            R.id.action_events -> if (currentFragment !is EventsFragment) {
                fragment = EventsFragment()
                Aardvark.firebaseAnalytics.setCurrentScreen(this, string(R.string.screen_events), null)
            }
            R.id.action_map -> if (currentFragment !is MapFragment) {
                fragment = MapFragment()
                Aardvark.firebaseAnalytics.setCurrentScreen(this, string(R.string.screen_map), null)
            }
            else -> fragment = PlaceholderFragment()
        }

        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
            return true
        }

        return false
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

    private fun setupFields() {
        client = OkHttpClient()
        request = Request.Builder().url(string(R.string.url_rest_api)).build()
        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        eventAdapter = moshi.adapter(Event::class.java)
    }

    private fun loadEvents() {
        doAsync {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Crashlytics.log(String.format(
                            Locale.getDefault(),
                            string(R.string.log_message_fail_event_load),
                            e.message
                    ))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Crashlytics.log(String.format(
                                Locale.getDefault(),
                                string(R.string.log_message_response_unsuccessful),
                                response.toString()
                        ))
                    }

                    val bufferedReader = response.body()!!.byteStream().bufferedReader()
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            events.add(eventAdapter.fromJson(it)!!)
                        }
                    }
                }
            })
            uiThread {
                setupViews()
                checkForNewVersion(false)
            }
        }
    }

    private fun setupLocationUpdate() {
        if (Preferences.trackLocation) {
            fusedLocationProviderClient = FusedLocationProviderClient(this)
            locationRequest = LocationRequest()
            locationRequest.interval = TimeUnit.SECONDS.toMillis(60)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(5)
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        val transaction = supportFragmentManager.beginTransaction()
        when (Preferences.bottomNavigationSelectedItemId) {
            R.id.action_dashboard -> transaction.replace(R.id.content, PlaceholderFragment())
            R.id.action_events -> transaction.replace(R.id.content, EventsFragment())
            R.id.action_map -> transaction.replace(R.id.content, MapFragment())
            else -> transaction.replace(R.id.content, PlaceholderFragment())
        }
        transaction.commit()
    }

    private fun checkForNewVersion(force: Boolean) {
        try {
            val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
            val lastKnownVersionCode = Preferences.lastKnownVersionCode
            if (force || Preferences.showChangelog && lastKnownVersionCode < versionCode) {
                Preferences.lastKnownVersionCode = versionCode
                showChangelog(versionCode)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Crashlytics.logException(e)
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
        AlertDialog.Builder(this)
                .setTitle(versionName)
                .setView(content)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show()
    }

    private fun startLocationUpdates() {
        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
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

        const val CHECK_LOCATION_PERMISSION = 42

        var lastLocation: Location? = null

        var events = mutableListOf<Event>()
    }
}
