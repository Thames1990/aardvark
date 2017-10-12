package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventProvider
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.util.REQUEST_CODE_INTRO
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.changelog_bottom_sheet_dialog.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import ru.noties.markwon.Markwon
import java.io.BufferedReader
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dashboardFragment: PlaceholderFragment by lazy { PlaceholderFragment() }

    private val eventsFragment: EventsFragment by lazy { EventsFragment() }

    private val mapFragment: MapFragment by lazy { MapFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Preferences.isFirstLaunch) {
            startActivityForResult(IntroActivity::class.java, REQUEST_CODE_INTRO)
        } else start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            Preferences.isFirstLaunch = false
            start()
        }
    }

    override fun onBackPressed() {
        if (Preferences.confirmExit) {
            materialDialog {
                title(R.string.confirm_exit)
                positiveText(R.string.exit)
                onPositive { _, _ -> finishSlideOut() }
            }
        } else finishSlideOut()
    }

    override fun onCreateOptionsMenu(menu: Menu) =
            consume { menuInflater.inflate(R.menu.menu_main, menu) }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_show_changelog -> consume { checkForNewVersion(true) }
        R.id.action_settings -> consume { startActivity(PreferenceActivity::class.java) }
        else -> super.onOptionsItemSelected(item)
    }

    private fun start() {
        doAsync {
            events = EventProvider.load()
            uiThread {
                setupViews()
                checkForNewVersion()
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setupViews() {
        setSupportActionBar(toolbar)
        with(supportFragmentManager) {
            bottom_navigation.setOnNavigationItemSelectedListener { item ->
                with(beginTransaction()) {
                    fragments.forEach { hide(it) }
                    when (item.itemId) {
                        R.id.action_dashboard -> {
                            if (dashboardFragment.isAdded) show(dashboardFragment)
                            else add(R.id.content, dashboardFragment)
                        }
                        R.id.action_events -> {
                            if (eventsFragment.isAdded) show(eventsFragment)
                            else add(R.id.content, eventsFragment)
                            eventsFragment.setHasOptionsMenu(true)
                        }
                        R.id.action_map -> {
                            if (mapFragment.isAdded) show(mapFragment)
                            else add(R.id.content, mapFragment)
                            mapFragment.setHasOptionsMenu(true)
                        }
                    }
                    consume { commit() }
                }
            }
        }
        bottom_navigation.selectedItemId = R.id.action_dashboard
    }

    private fun checkForNewVersion(force: Boolean = false) {
        val versionCode = BuildConfig.VERSION_CODE
        if (force || Preferences.showChangelog && Preferences.version < versionCode) {
            Preferences.version = versionCode
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
        } else showChangelogDialog(versionName, changelog)
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

    companion object {

        lateinit var events: List<Event>
    }
}
