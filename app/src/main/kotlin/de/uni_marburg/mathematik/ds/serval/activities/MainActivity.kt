package de.uni_marburg.mathematik.ds.serval.activities

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.utils.*
import com.google.android.gms.maps.model.LatLng
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.typeface.IIcon
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.SwipeToggleViewPager
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayouts
import de.uni_marburg.mathematik.ds.serval.enums.TabItems
import de.uni_marburg.mathematik.ds.serval.fragments.BaseFragment
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.settings.*
import de.uni_marburg.mathematik.ds.serval.utils.*
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider

class MainActivity : BaseActivity() {

    private val appBar: AppBarLayout by bindView(R.id.appbar)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val viewPager: SwipeToggleViewPager by bindView(R.id.view_pager)

    private val pagerAdapter = SectionsPagerAdapter()

    private lateinit var locationControl: SmartLocation.LocationControl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(AppearancePrefs.MainActivityLayout.layoutRes)
        setSupportActionBar(toolbar)
        setColors {
            toolbar(toolbar)
            themeWindow = false
            header(appBar)
            background(viewPager)
        }

        setupAppBar()
        setupViewPager()
        setupTabLayout()

        if (hasLocationPermission) trackLocation()

        checkForNewVersion()
    }

    override fun onPause() {
        super.onPause()
        if (::locationControl.isInitialized) locationControl.stop()
    }

    override fun onResume() {
        super.onResume()
        if (hasLocationPermission) trackLocation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS -> {
                if (resultCode and REQUEST_RESTART > 0) restart()
                if (resultCode and REQUEST_APPLICATION_RESTART > 0) restartApplication()
                if (resultCode and REQUEST_NAV > 0) themeNavigationBar()
                if (resultCode and RELOAD_EVENTS > 0) eventViewModel.getFromRepository(deleteEvents = true)
            }
        }
    }

    override fun backConsumer(): Boolean {
        if (BehaviourPrefs.confirmExit) {
            materialDialogThemed {
                title(R.string.kau_exit)
                content(R.string.kau_exit_confirmation)
                positiveText(R.string.kau_yes)
                negativeText(R.string.kau_no)
                onPositive { _, _ -> finish() }
                checkBoxPromptRes(R.string.kau_do_not_show_again, false, { _, isChecked ->
                    BehaviourPrefs.confirmExit = !isChecked
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
            color = AppearancePrefs.Theme.iconColor,
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
        if (AppearancePrefs.MainActivityLayout.layout == MainActivityLayouts.BOTTOM_BAR) {
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                viewPager.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    withMargins(
                        left = 0,
                        top = 0,
                        right = 0,
                        bottom = appBarLayout.measuredHeight + verticalOffset
                    )
                }
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
            TabItems.values().map {
                val badgedIcon = BadgedIcon(context).apply { iicon = it.iicon }
                val tab: TabLayout.Tab = newTab().setCustomView(badgedIcon)
                addTab(tab)
            }

            setSelectedTabIndicatorColor(AppearancePrefs.MainActivityLayout.iconColor)
            setBackgroundColor(AppearancePrefs.MainActivityLayout.backgroundColor)

            addOnTabSelectedListener(object : TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val currentTab: Int = tab.position
                    val currentFragment: Fragment = pagerAdapter.getItem(currentTab)

                    viewPager.item = currentTab

                    when (currentFragment) {
                        is DashboardFragment -> selectDashboardFragment()
                        is EventsFragment -> selectEventsFragmentTab(currentFragment)
                        is MapFragment -> selectMapFragmentTab(currentFragment)
                    }

                    appBar.expand()
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

        fun submitEvents(pagedList: PagedList<Event>?) {
            val eventCount: Int = pagedList?.size ?: 0
            val tab: TabLayout.Tab? = tabs.getTabAt(1)
            val badgedIcon = tab?.customView as BadgedIcon
            badgedIcon.badgeText = eventCount.toString()
        }

        observe(liveData = eventViewModel.pagedList, onChanged = ::submitEvents)
    }

    private fun selectDashboardFragment() = fab.hide()

    private fun selectEventsFragmentTab(currentFragment: EventsFragment) = with(fab) {
        currentFragment.bindFab(fab = this)
        showWithOptions(
            icon = GoogleMaterial.Icon.gmd_arrow_upward,
            tooltipTextRes = R.string.tooltip_fab_scroll_to_top,
            onClickListener = {
                appBar.expand()
                currentFragment.scrollToTop()
            }
        )
    }

    private fun selectMapFragmentTab(currentFragment: MapFragment) = fab.showWithOptions(
        icon = GoogleMaterial.Icon.gmd_my_location,
        tooltipTextRes = R.string.tooltip_fab_move_to_current_location,
        onClickListener = {
            appBar.expand()
            currentFragment.moveToPosition(devicePosition)
        },
        show = hasLocationPermission && MapPrefs.myLocationButtonEnabled
    )

    private fun trackLocation() {
        val locationLiveData = LocationLiveData(this)
        locationControl = locationLiveData.locationControl

        // Get last location
        locationControl.oneFix().apply { start { location -> deviceLocation = location } }

        fun submitLocation(location: Location?) {
            if (location != null) deviceLocation = location
        }

        // Get notified about location changes
        observe(liveData = locationLiveData, onChanged = ::submitLocation)
    }

    private fun checkForNewVersion() {
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (BehaviourPrefs.showChangelog) showChangelog()
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

        private val fragments = mutableListOf(
            DashboardFragment(),
            EventsFragment(),
            MapFragment()
        )

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment: BaseFragment = super.instantiateItem(container, position) as BaseFragment
            fragments[position] = fragment
            return fragment
        }

        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }

    private class LocationLiveData(context: Context) : LiveData<Location>() {

        private val locationParams: LocationParams = LocationParams.Builder()
            .setAccuracy(LocationPrefs.LocationRequestAccuracy.accuracy)
            .setDistance(LocationPrefs.distance.toFloat())
            .setInterval(LocationPrefs.interval.toLong())
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

    private class BadgedIcon(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr) {

        private val badgeTextView: TextView by bindView(R.id.badge_text)
        private val badgeImage: ImageView by bindView(R.id.badge_image)

        init {
            View.inflate(context, R.layout.view_badged_icon, this)
            val badgeColor = AppearancePrefs.MainActivityLayout.backgroundColor
                .withAlpha(255)
                .colorToForeground(0.2f)
            val badgeBackground = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                intArrayOf(badgeColor, badgeColor)
            ).apply {
                cornerRadius = 13.dpToPx.toFloat()
            }
            with(badgeTextView) {
                background = badgeBackground
                setTextColor(AppearancePrefs.MainActivityLayout.iconColor)
            }
        }

        var iicon: IIcon? = null
            set(value) {
                field = value
                badgeImage.setIconWithOptions(
                    icon = value,
                    sizeDp = 20,
                    color = AppearancePrefs.MainActivityLayout.iconColor
                )
            }

        var badgeText: String?
            get() = badgeTextView.text.toString()
            set(value) {
                if (badgeTextView.text == value) return
                badgeTextView.text = value
                badgeTextView.visibleIf(value != null && value != "0")
            }

    }

    companion object {
        const val ACTIVITY_SETTINGS = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_APPLICATION_RESTART = 1 shl 3
        const val REQUEST_NAV = 1 shl 4
        const val RELOAD_EVENTS = 1 shl 5

        var deviceLocation = Location(BuildConfig.APPLICATION_ID)
        val devicePosition: LatLng
            get() = LatLng(deviceLocation.latitude, deviceLocation.longitude)
    }

}
