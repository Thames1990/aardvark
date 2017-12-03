package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.xml.showChangelog
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
            startActivityForResult(IntroActivity::class.java, INTRO_REQUEST_CODE)
        } else start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTRO_REQUEST_CODE) start()
    }

    override fun onBackPressed() {
        if (Prefs.confirmExit) materialDialog {
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> consume { startActivity(PreferenceActivity::class.java) }
        else                 -> super.onOptionsItemSelected(item)
    }

    private fun start() {
        setSecureFlag()
        setCurrentScreen()
        doAsync {
            events = if (isNetworkAvailable) EventRepository.fetch() else emptyList()
            uiThread {
                setTheme(R.style.AppTheme)
                ProcessLifecycleOwner.get().lifecycle.addObserver(this@MainActivity)
                setContentView(R.layout.activity_main)
                setupViews()
                checkForNewVersion()
            }
        }
    }

    // TODO: This fixes memory leaks for now. Should be switched back to ON_START.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun authenticate() = startActivity(FingerprintActivity::class.java)

    /**
     * Checks if a new version of the app was detected.
     *
     * If a new version is detected and the user wants to view changelogs, the changelog is shown.
     */
    private fun checkForNewVersion() {
        if (Prefs.showChangelog && Prefs.version < BuildConfig.VERSION_CODE) {
            Prefs.version = BuildConfig.VERSION_CODE
            showChangelog(R.xml.changelog) {
                title(R.string.kau_changelog)
                positiveText(android.R.string.ok)
            }
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

        lateinit var events: List<Event>
    }
}
