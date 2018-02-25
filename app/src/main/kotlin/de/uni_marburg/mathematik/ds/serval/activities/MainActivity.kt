package de.uni_marburg.mathematik.ds.serval.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.TabItem
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.LocationLiveData
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.AardvarkViewPager
import de.uni_marburg.mathematik.ds.serval.views.BadgedIcon
import kerval.connection.ProgressEvent
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseActivity() {

    val appBar: AppBarLayout by bindView(R.id.appbar)
    val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var eventViewModel: EventViewModel

    private val progressBar: MaterialProgressBar by bindView(R.id.progressBar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val viewPager: AardvarkViewPager by bindView(R.id.container)

    companion object {
        const val ACTIVITY_SETTINGS = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_APPLICATION_RESTART = 1 shl 3
        const val REQUEST_NAV = 1 shl 4

        var lastLocation = Location(BuildConfig.APPLICATION_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(Prefs.mainActivityLayout.layoutRes)
        setSupportActionBar(toolbar)
        setColors {
            toolbar(toolbar)
            themeWindow = false
            header(appBar)
            background(viewPager)
        }

        viewPager.setup()
        tabs.setup()

        with(fab) {
            backgroundTintList = ColorStateList.valueOf(Prefs.headerColor)
            setIcon(icon = GoogleMaterial.Icon.gmd_arrow_upward, color = Prefs.iconColor)
        }

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.events.observe(this, Observer { tabs.reload() })

        if (hasLocationPermission) {
            LocationLiveData(this).observe(this, Observer { location ->
                if (location != null) lastLocation = location
            })
        }

        if (Prefs.showDownloadProgress) {
            progressBar.visible()
            EventRepository.progressObservable
                .subscribe { progressEvent ->
                    val progress: Byte = progressEvent.progress
                    if (progress != ProgressEvent.PROGRESS_FAILURE) {
                        // TODO Save progress as Int again...
                        progressBar.progress = progress.toInt()
                    }
                }
        }

        checkForNewVersion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS -> {
                if (resultCode and REQUEST_RESTART > 0) restart()
                if (resultCode and REQUEST_APPLICATION_RESTART > 0) restartApplication()
                if (resultCode and REQUEST_NAV > 0) themeNavigationBar()
            }
        }
    }

    override fun backConsumer(): Boolean {
        if (Prefs.exitConfirmation) {
            materialDialogThemed {
                title(R.string.kau_exit)
                content(R.string.kau_exit_confirmation)
                positiveText(R.string.kau_yes)
                negativeText(R.string.kau_no)
                onPositive { _, _ -> finish() }
                checkBoxPromptRes(R.string.kau_do_not_show_again, false, { _, isChecked ->
                    Prefs.exitConfirmation = !isChecked
                })
            }
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        setMenuIcons(
            menu = menu,
            color = Prefs.iconColor,
            iicons = *arrayOf(R.id.action_settings to GoogleMaterial.Icon.gmd_settings)
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivityForResult<SettingsActivity>(
                    requestCode = ACTIVITY_SETTINGS,
                    bundleBuilder = {
                        withCustomAnimation(
                            context = this@MainActivity,
                            enterResId = R.anim.kau_slide_in_right,
                            exitResId = R.anim.kau_fade_out
                        )
                    }
                )
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun AardvarkViewPager.setup() {
        val sectionsPagerAdapter = SectionsPagerAdapter()
        adapter = sectionsPagerAdapter
        offscreenPageLimit = sectionsPagerAdapter.count
        addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
    }

    private fun TabLayout.setup() {
        setBackgroundColor(Prefs.mainActivityLayout.backgroundColor)

        addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {
                    if (Prefs.animate) fab.fadeIn()
                    else fab.gone()
                } else {
                    if (Prefs.animate) fab.fadeOut()
                    else fab.visible()
                }
                viewPager.setCurrentItem(tab.position, Prefs.animate)
                appBar.setExpanded(true, Prefs.animate)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (isNetworkAvailable) eventViewModel.reload()
                else snackbarThemed(string(R.string.network_disconnected))
            }
        })

        TabItem.values().map {
            val badgedIcon = BadgedIcon(this@MainActivity).apply { iicon = it.iicon }
            val tab: TabLayout.Tab = newTab().setCustomView(badgedIcon)
            addTab(tab)
        }
    }

    private fun TabLayout.reload() {
        doAsync {
            val eventCount: Int = eventViewModel.count()
            uiThread {
                val tab: TabLayout.Tab? = getTabAt(1)
                val badgedIcon = tab?.customView as BadgedIcon
                badgedIcon.badgeText = eventCount.toString()
            }
        }
    }

    private fun checkForNewVersion() {
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (!BuildConfig.DEBUG) showChangelog()
            aardvarkAnswersCustom(
                name = "Version",
                events = *arrayOf(
                    "Version code" to BuildConfig.VERSION_CODE,
                    "Version name" to BuildConfig.VERSION_NAME,
                    "Build type" to BuildConfig.BUILD_TYPE,
                    "Aardvark id" to Aardvark.aardvarkId
                )
            )
        }
    }

    inner class SectionsPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        private val fragments = listOf(
            DashboardFragment(),
            EventsFragment(),
            MapFragment()
        )

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }
}
