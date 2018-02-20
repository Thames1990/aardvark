package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AardvarkItem
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.model.location.LocationViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import io.reactivex.schedulers.Schedulers
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var locationViewModel: LocationViewModel

    private val appBar: AppBarLayout by bindView(R.id.appbar)
    private val bottomNavigation: AHBottomNavigation by bindView(R.id.bottom_navigation)
    private val progressBar: MaterialProgressBar by bindView(R.id.progressBar)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val viewPager: AHBottomNavigationViewPager by bindView(R.id.container)

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
        }

        val aardvarkAdapter = AardvarkAdapter(context = this, fm = supportFragmentManager)
        viewPager.apply {
            adapter = aardvarkAdapter
            offscreenPageLimit = aardvarkAdapter.count

            setPagingEnabled(Prefs.usePaging)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) = Unit

                override fun onPageSelected(position: Int) {
                    bottomNavigation.currentItem = position
                }

            })
        }
        bottomNavigation.setup()

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)
        eventViewModel.events.observe(this, Observer { bottomNavigation.reloadTabs() })

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        locationViewModel.location.observe(this, Observer { location ->
            if (location != null) lastLocation = location
        })

        if (Prefs.useProgressBar) {
            progressBar.visible()
            EventRepository.progressObservable
                .observeOn(Schedulers.computation())
                .subscribe { progressEvent ->
                    if (progressEvent.percentIsAvailable)
                        progressBar.progress = progressEvent.progress
                }
        }

        checkForNewVersion()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS -> {
                // TODO Use ActivityUtils restart() and restartApplication() once Kau updates
                if (resultCode and REQUEST_RESTART > 0) restartActivity()
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

    private fun AHBottomNavigation.setup() {
        AardvarkItem.values().map { aardvarkItem ->
            val bottomNavigationItem = AHBottomNavigationItem(
                string(aardvarkItem.titleRes),
                aardvarkItem.iicon.toDrawable(context)
            )
            addItem(bottomNavigationItem)
        }

        accentColor = Prefs.accentColor
        defaultBackgroundColor = Prefs.backgroundColor
        inactiveColor = Prefs.backgroundColor.colorToForeground(0.2f)

        setItemDisableColor(Prefs.iconColor.darken(0.8f))
        setNotificationBackgroundColor(Prefs.accentColor)
        setOnTabSelectedListener { position, wasReselected ->
            if (wasReselected) {
                if (isNetworkAvailable) eventViewModel.reload()
                else snackbarThemed(string(R.string.network_disconnected))
            } else {
                viewPager.setCurrentItem(position, Prefs.animate)
                appBar.setExpanded(true, Prefs.animate)
            }
            true
        }
    }

    private fun AHBottomNavigation.reloadTabs() {
        doAsync {
            val eventCount: Int = eventViewModel.count()
            uiThread { setNotification(eventCount.toString(), 1) }
        }
    }

    private fun checkForNewVersion() {
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (!BuildConfig.DEBUG) aardvarkChangelog()
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
}

class AardvarkAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = listOf(
        DashboardFragment(),
        EventsFragment(),
        MapFragment()
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

}
