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
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import ca.allanwang.kau.utils.*
import com.google.android.gms.maps.model.LatLng
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
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

    val appBar: AppBarLayout by bindView(R.id.appbar)
    val fab: FloatingActionButton by bindView(R.id.fab)

    private val progressBar: MaterialProgressBar by bindView(R.id.progressBar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val viewPager: SwipeToggleViewPager by bindView(R.id.container)

    private val eventViewModel: EventViewModel by lazy {
        ViewModelProviders.of(this).get(EventViewModel::class.java)
    }

    private val sectionsPagerAdapter = SectionsPagerAdapter()

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

        fab.backgroundTintList = ColorStateList.valueOf(Prefs.headerColor)

        eventViewModel.events.observe(this, Observer { tabs.reload() })

        if (hasLocationPermission) {
            val currentLocation = CurrentLocation(this)

            // Get last location
            val oneFix = currentLocation.locationControl.oneFix()
            oneFix.start { location ->
                lastLocation = location
                oneFix.stop()
            }

            // Get notified about location changes
            currentLocation.observe(this, Observer<Location> { location ->
                location?.let { lastLocation = it }
            })
        }

        if (Prefs.showDownloadProgress) {
            progressBar.visible()
            EventRepository.progressObservable?.subscribe { progressEvent ->
                val progress: Long = progressEvent.progress
                if (progress != ProgressEvent.NO_CONTENT_AVAILABLE) {
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
        if (Prefs.confirmExit) {
            materialDialogThemed {
                title(R.string.kau_exit)
                content(R.string.kau_exit_confirmation)
                positiveText(R.string.kau_yes)
                negativeText(R.string.kau_no)
                onPositive { _, _ -> finish() }
                checkBoxPromptRes(R.string.kau_do_not_show_again, false, { _, isChecked ->
                    Prefs.confirmExit = !isChecked
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

    private fun SwipeToggleViewPager.setup() {
        adapter = sectionsPagerAdapter
        offscreenPageLimit = sectionsPagerAdapter.count - 1
        addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
    }

    private fun TabLayout.setup() {
        TabItem.values().map {
            val badgedIcon = BadgedIcon(context).apply { iicon = it.iicon }
            val tab: TabLayout.Tab = newTab().setCustomView(badgedIcon)
            addTab(tab)
        }

        setSelectedTabIndicatorColor(Prefs.mainActivityLayout.iconColor)
        setBackgroundColor(Prefs.mainActivityLayout.backgroundColor)

        addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @SuppressLint("NewApi")
            override fun onTabSelected(tab: TabLayout.Tab) {
                val currentTab: Int = tab.position

                viewPager.setCurrentItem(currentTab, Prefs.animate)
                appBar.setExpanded(true, Prefs.animate)

                with(fab) {
                    val currentFragment: Fragment = sectionsPagerAdapter.getItem(currentTab)
                    when (currentFragment) {
                        is DashboardFragment -> {
                            hide()
                            setOnClickListener { appBar.setExpanded(true, Prefs.animate) }
                        }
                        is EventsFragment -> {
                            show()
                            hideOnDownwardsScroll(currentFragment.recyclerView)
                            setIcon(
                                icon = GoogleMaterial.Icon.gmd_arrow_upward,
                                color = Prefs.iconColor
                            )
                            setOnClickListener {
                                appBar.setExpanded(true, Prefs.animate)
                                currentFragment.recyclerView.smoothScrollToPosition(0)
                            }
                            if (buildIsOreoAndUp) tooltipText = string(R.string.event_reload)
                        }
                        is MapFragment -> {
                            show()
                            setIcon(
                                icon = GoogleMaterial.Icon.gmd_my_location,
                                color = Prefs.iconColor
                            )
                            setOnClickListener {
                                appBar.setExpanded(true, Prefs.animate)
                                val position = LatLng(lastLocation.latitude, lastLocation.longitude)
                                currentFragment.cameraUpdate(position)
                            }
                            if (buildIsOreoAndUp) {
                                tooltipText = string(R.string.location_move_to_current)
                            }
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                if (isNetworkAvailable) {
                    val rotateAnimation = RotateAnimation(
                        0f,
                        360f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    ).apply {
                        duration = 1500L
                        interpolator = AnimHolder.decelerateInterpolator(context)
                        repeatCount = Animation.INFINITE
                    }

                    val badgedIcon = tab.customView!! as BadgedIcon
                    val icon = badgedIcon.iicon
                    with(badgedIcon) {
                        startAnimation(rotateAnimation)
                        iicon = GoogleMaterial.Icon.gmd_autorenew
                        badgeText = null
                    }

                    doAsync {
                        eventViewModel.reload()
                        uiThread {
                            rotateAnimation.repeatCount = 0
                            badgedIcon.iicon = icon
                        }
                    }
                } else viewPager.snackbarThemed(string(R.string.network_disconnected))
            }
        })
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
            if (!BuildConfig.DEBUG && Prefs.showChangelog) showChangelog()
            answersCustom(
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

    private class CurrentLocation(context: Context) : LiveData<Location>() {

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

        var lastLocation = Location(BuildConfig.APPLICATION_ID)
    }

}
