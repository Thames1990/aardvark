package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import ca.allanwang.kau.xml.showChangelog
import com.github.ajalt.reprint.core.AuthenticationFailureReason
import com.github.ajalt.reprint.core.AuthenticationListener
import com.github.ajalt.reprint.core.Reprint
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.util.*
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.FingerprintFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fingerprint.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), LifecycleObserver {

    private val dashboardFragment: DashboardFragment by lazy { DashboardFragment() }

    private val eventsFragment: EventsFragment by lazy { EventsFragment() }

    private val mapFragment: MapFragment by lazy { MapFragment() }

    private val fingerprintFragment: FingerprintFragment by lazy { FingerprintFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Preferences.isFirstLaunch) {
            startActivityForResult(IntroActivity::class.java, INTRO_REQUEST_CODE)
        } else start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INTRO_REQUEST_CODE) start()
    }

    override fun onBackPressed() {
        if (Preferences.confirmExit) materialDialog {
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
        settingsIcon.setColorFilter(color(android.R.color.white), PorterDuff.Mode.SRC_IN)
        settings.icon = settingsIcon
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
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun authenticate() = with(supportFragmentManager) {
        if (!fingerprintFragment.isAdded) {
            beginTransaction().add(android.R.id.content, fingerprintFragment).commit()
        }
        Reprint.authenticate(object : AuthenticationListener {
            override fun onSuccess(moduleTag: Int) {
                beginTransaction().remove(fingerprintFragment).commit()
                checkForNewVersion()
            }

            override fun onFailure(
                    failureReason: AuthenticationFailureReason?,
                    fatal: Boolean,
                    errorMessage: CharSequence?,
                    moduleTag: Int,
                    errorCode: Int
            ) {
                fingerprintFragment.description.text = errorMessage
            }
        })
    }

    /**
     * Checks if a new version of the app was detected.
     *
     * If a new version is detected and the user wants to view changelogs, the changelog is shown.
     */
    private fun checkForNewVersion() {
        if (Preferences.showChangelog && Preferences.version < BuildConfig.VERSION_CODE) {
            Preferences.version = BuildConfig.VERSION_CODE
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
