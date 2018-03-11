package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import ca.allanwang.kau.utils.*
import com.google.android.gms.maps.model.LatLng
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.TabItem
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.EventRepository
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.BadgedIcon
import de.uni_marburg.mathematik.ds.serval.views.SwipeToggleViewPager
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider
import kerval.connection.ProgressEvent
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseActivity() {

    private val appBar: AppBarLayout by bindView(R.id.appbar)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val progressBar: MaterialProgressBar by bindView(R.id.progressBar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val viewPager: SwipeToggleViewPager by bindView(R.id.view_pager)

    private lateinit var pagerAdapter: SectionsPagerAdapter
    private lateinit var viewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pagerAdapter = SectionsPagerAdapter()
        viewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)

        setContentView(Prefs.Appearance.mainActivityLayout.layoutRes)
        setSupportActionBar(toolbar)
        setColors {
            toolbar(toolbar)
            themeWindow = false
            header(appBar)
            background(viewPager)
        }
        fab.backgroundTintList =
                ColorStateList.valueOf(Prefs.Appearance.headerColor.withMinAlpha(200))

        setupAppBar()
        setupViewPager()
        setupTabLayout()

        if (hasLocationPermission) trackLocation()
        if (Prefs.Experimental.showDownloadProgress) trackEventDownloadProgress()

        checkForNewVersion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS -> {
                if (resultCode and REQUEST_RESTART > 0) restart()
                if (resultCode and REQUEST_APPLICATION_RESTART > 0) restartApplication()
                if (resultCode and REQUEST_NAV > 0) themeNavigationBar()
                if (resultCode and RELOAD_EVENTS > 0) doAsync { viewModel.fetchEvents(deleteEvents = true) }
            }
        }
    }

    override fun backConsumer(): Boolean {
        if (Prefs.Behaviour.confirmExit) {
            materialDialogThemed {
                title(R.string.kau_exit)
                content(R.string.kau_exit_confirmation)
                positiveText(R.string.kau_yes)
                negativeText(R.string.kau_no)
                onPositive { _, _ -> finish() }
                checkBoxPromptRes(R.string.kau_do_not_show_again, false, { _, isChecked ->
                    Prefs.Behaviour.confirmExit = !isChecked
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
            color = Prefs.Appearance.mainActivityLayout.iconColor,
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

    private fun setupAppBar() {
        // Fixes bottom layout cutoff
        if (Prefs.Appearance.mainActivityLayout == MainActivityLayout.BOTTOM_BAR) {
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                val layoutParams = viewPager.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, appBarLayout.measuredHeight + verticalOffset)
                viewPager.requestLayout()
            }
        }
    }

    private fun setupViewPager() = with(viewPager) {
        adapter = pagerAdapter
        offscreenPageLimit = pagerAdapter.count - 1
        addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
    }

    private fun setupTabLayout() {
        with(tabs) {
            TabItem.values().map {
                val badgedIcon = BadgedIcon(context).apply { iicon = it.iicon }
                val tab: TabLayout.Tab = newTab().setCustomView(badgedIcon)
                addTab(tab)
            }

            setSelectedTabIndicatorColor(Prefs.Appearance.mainActivityLayout.iconColor)
            setBackgroundColor(Prefs.Appearance.mainActivityLayout.backgroundColor)

            addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val currentTab: Int = tab.position
                    val currentFragment: Fragment = pagerAdapter.getItem(currentTab)

                    viewPager.item = currentTab
                    appBar.expand()

                    when (currentFragment) {
                        is DashboardFragment -> selectDashboardFragment()
                        is EventsFragment -> {
                            checkNotNull(currentFragment.recyclerView)
                            selectEventsFragmentTab(currentFragment.recyclerView)
                        }
                        is MapFragment -> selectMapFragmentTab(currentFragment)
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    val currentTab: Int = tab.position
                    val currentFragment: Fragment = pagerAdapter.getItem(currentTab)

                    when (currentFragment) {
                        is DashboardFragment -> Unit
                        is EventsFragment -> currentFragment.reloadEvents()
                        is MapFragment -> currentFragment.zoomToAllMarkers()
                    }
                }
            })
        }

        viewModel.events.observe(this, Observer { reloadTabBadges() })
    }

    private fun selectDashboardFragment() {
        fab.hide()
        tabs.setOnClickListener { appBar.expand() }
    }

    @SuppressLint("NewApi")
    private fun selectEventsFragmentTab(recyclerView: RecyclerView) {
        with(fab) {
            setOnClickListener {
                appBar.expand()
                recyclerView.scrollToPosition(0)
            }
            hideOnDownwardsScroll(recyclerView)
            setIcon(
                icon = GoogleMaterial.Icon.gmd_arrow_upward,
                color = Prefs.Appearance.mainActivityLayout.iconColor
            )
            if (buildIsOreoAndUp) tooltipText = string(R.string.tooltip_fab_scroll_to_top)
            show()
        }

        tabs.setOnClickListener {
            appBar.expand()
            recyclerView.smoothScrollToPosition(0)
        }
    }

    @SuppressLint("NewApi")
    private fun selectMapFragmentTab(currentFragment: MapFragment) {
        with(fab) {
            setOnClickListener {
                appBar.expand()
                currentFragment.moveToPosition(lastPosition)
            }
            setIcon(
                icon = GoogleMaterial.Icon.gmd_my_location,
                color = Prefs.Appearance.mainActivityLayout.iconColor
            )
            if (buildIsOreoAndUp) {
                tooltipText = string(R.string.tooltip_fab_move_to_current_location)
            }
            visibleIf(hasLocationPermission && Prefs.Map.isMyLocationButtonEnabled)
        }
    }

    private fun reloadTabBadges() {
        doAsync {
            val eventCount: Int = viewModel.eventCount
            uiThread {
                val tab: TabLayout.Tab? = tabs.getTabAt(1)
                val badgedIcon = tab?.customView as BadgedIcon
                badgedIcon.badgeText = eventCount.toString()
            }
        }
    }

    private fun trackLocation() {
        val locationLiveData = LocationLiveData(this)

        // Get last location
        val oneFix = locationLiveData.locationControl.oneFix()
        oneFix.start { location -> lastLocation = location }

        // Get notified about location changes
        locationLiveData.observe(this, Observer<Location> { location ->
            if (location != null) lastLocation = location
        })
    }

    private fun trackEventDownloadProgress() {
        progressBar.visible()
        EventRepository.progressObservable?.subscribe { progressEvent ->
            val progress: Long = progressEvent.progress
            if (progress != ProgressEvent.NO_CONTENT_AVAILABLE) {
                progressBar.progress = progress.toInt()
            }
        }
    }

    private fun checkForNewVersion() {
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (Prefs.Behaviour.showChangelog) showChangelog()
            logAnalytics(
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

    private inner class SectionsPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

        private val fragments = listOf(
            DashboardFragment(),
            EventsFragment(),
            MapFragment()
        )

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }

    private class LocationLiveData(context: Context) : LiveData<Location>() {

        private val locationParams: LocationParams = LocationParams.Builder()
            .setAccuracy(Prefs.locationRequestAccuracy.accuracy)
            .setDistance(Prefs.locationRequestDistance.toFloat())
            .setInterval(Prefs.locationRequestInterval.toLong())
            .build()

        val locationControl: SmartLocation.LocationControl = SmartLocation.with(context)
            .location(LocationGooglePlayServicesWithFallbackProvider(context))
            .config(locationParams)

        override fun onActive() {
            super.onActive()
            locationControl.start { location -> value = location }
        }

        override fun onInactive() {
            locationControl.stop()
            super.onInactive()
        }

    }

    companion object {
        const val ACTIVITY_SETTINGS = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_APPLICATION_RESTART = 1 shl 3
        const val REQUEST_NAV = 1 shl 4
        const val RELOAD_EVENTS = 1 shl 5

        var lastLocation = Location(BuildConfig.APPLICATION_ID)
        val lastPosition: LatLng
            get() = LatLng(lastLocation.latitude, lastLocation.longitude)
    }

}
