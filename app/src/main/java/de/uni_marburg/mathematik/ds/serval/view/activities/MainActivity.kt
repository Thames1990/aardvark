package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.arch.lifecycle.LifecycleObserver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.util.*
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private val dashboardFragment: DashboardFragment by lazy { DashboardFragment() }

    private val eventsFragment: EventsFragment by lazy { EventsFragment() }

    private val mapFragment: MapFragment by lazy { MapFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.isFirstLaunch) {
            startActivityForResult(IntroActivity2::class.java, INTRO_REQUEST_CODE)
        } else start()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS  -> {
                // Completely restart application
                if (resultCode and REQUEST_RESTART_APPLICATION > 0) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    val pending = PendingIntent.getActivity(
                            this,
                            666,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    )
                    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (buildIsMarshmallowAndUp) {
                        alarm.setExactAndAllowWhileIdle(
                                AlarmManager.RTC,
                                System.currentTimeMillis() + 100,
                                pending
                        )
                    } else alarm.setExact(
                            AlarmManager.RTC,
                            System.currentTimeMillis() + 100,
                            pending
                    )
                    finish()
                    System.exit(0)
                    return
                }
                if (resultCode and REQUEST_RESTART > 0 && data != null) return restart()
                if (resultCode and REQUEST_NAV > 0) aardvarkNavigationBar()
            }
            INTRO_REQUEST_CODE -> start()
        }
    }

    override fun onBackPressed() {
        if (Prefs.exitConfirmation) materialDialogThemed {
            title(R.string.preference_confirm_exit)
            negativeText(android.R.string.cancel)
            positiveText(android.R.string.ok)
            onPositive { _, _ -> finishSlideOut() }
        } else finishSlideOut()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume {
        menuInflater.inflate(R.menu.menu_main, menu)
        val settings = menu.findItem(R.id.action_settings)
        val settingsIcon = settings.icon
        settingsIcon.tint(color(android.R.color.white))
    }

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            consume {
                val intent = Intent(this, SettingsActivity::class.java)
                val bundle = ActivityOptionsCompat.makeCustomAnimation(
                        this,
                        R.anim.kau_slide_in_right,
                        R.anim.kau_fade_out
                ).toBundle()
                startActivityForResult(intent, ACTIVITY_SETTINGS, bundle)
            }
        }
        else                 -> super.onOptionsItemSelected(item)
    }

    private fun start() {
        setSecureFlag()
        setCurrentScreen()
        doAsync {
            events = if (isNetworkAvailable) EventRepository.fetch() else emptyList()
            uiThread {
                setTheme(R.style.AppTheme)
                setContentView(R.layout.activity_main)
                setupViews()
                checkForNewVersion()
            }
        }
    }

    /**
     * Checks if a new version of the app was detected.
     *
     * If a new version is detected and the user wants to view changelogs, the changelog is shown.
     */
    private fun checkForNewVersion() {
        if (Prefs.changelog && Prefs.versionCode < BuildConfig.VERSION_CODE) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            aardvarkChangelog()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setupViews() {
        setSupportActionBar(toolbar)
        with(supportFragmentManager) {
            with(beginTransaction()) {
                add(R.id.content, mapFragment)
                add(R.id.content, eventsFragment)
                add(R.id.content, dashboardFragment)
                commit()
            }
            bottom_navigation.setOnNavigationItemSelectedListener { item ->
                with(beginTransaction()) {
                    fragments.forEach { hide(it) }
                    when (item.itemId) {
                        R.id.action_dashboard -> show(dashboardFragment)
                        R.id.action_events    -> {
                            show(eventsFragment)
                            eventsFragment.setHasOptionsMenu(true)
                        }
                        R.id.action_map       -> {
                            show(mapFragment)
                            mapFragment.setHasOptionsMenu(true)
                        }
                    }
                    consume { commit() }
                }
            }
        }
        bottom_navigation.selectedItemId = R.id.action_dashboard
    }

    companion object {
        const val ACTIVITY_SETTINGS = 97
        const val REQUEST_RESTART_APPLICATION = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_NAV = 1 shl 3

        lateinit var events: List<Event>
    }
}
