package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import co.zsmb.materialdrawerkt.builders.Builder
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import co.zsmb.materialdrawerkt.draweritems.profile.profileSetting
import com.crashlytics.android.answers.ContentViewEvent
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AardvarkItem
import de.uni_marburg.mathematik.ds.serval.fragments.BaseFragment
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.BadgedIcon
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseActivity() {

    private lateinit var drawer: Drawer
    private lateinit var drawerHeader: AccountHeader

    // TODO Replace Kotterknife with Kotlin Android Extension synthetic views
    private val appBar: AppBarLayout by bindView(R.id.appbar)
    private val coordinator: CoordinatorLayout by bindView(R.id.main_content)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val dashboardFragment by lazy { DashboardFragment() }
    private val eventsFragment by lazy { EventsFragment() }
    private val mapFragment by lazy { MapFragment() }

    private val fragments: Array<Fragment> = arrayOf(dashboardFragment, eventsFragment, mapFragment)

    companion object {
        const val ACTIVITY_SETTINGS = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_NAV = 1 shl 3

        lateinit var events: List<Event>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (!BuildConfig.DEBUG) {
                aardvarkChangelog()
                aardvarkAnswersCustom(
                        "Version",
                        "Version code" to BuildConfig.VERSION_CODE,
                        "Version name" to BuildConfig.VERSION_NAME,
                        "Build type" to BuildConfig.BUILD_TYPE,
                        "Aardvark id" to Prefs.aardvarkId
                )
            }
        }
        setContentView(Prefs.mainActivityLayout.layoutRes)
        // TODO Set loading fragment
        loadEvents()
        setSupportActionBar(toolbar)
        setupDrawer(savedInstanceState)
        setAardvarkColors(toolbar, themeWindow = false, headers = arrayOf(appBar))
        tabs.setBackgroundColor(Prefs.mainActivityLayout.backgroundColor())
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS -> {
                if (resultCode and REQUEST_RESTART > 0) {
                    // Fix until i figure out how to properly use restart
                    startActivity(MainActivity::class.java)
                    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out)
                    finish()
                    overridePendingTransition(R.anim.kau_fade_in, R.anim.kau_fade_out)
                }
                if (resultCode and REQUEST_NAV > 0) aardvarkNavigationBar()
            }
        }
    }

    override fun backConsumer(): Boolean = consumeIf(Prefs.exitConfirmation) {
        materialDialogThemed {
            title(R.string.kau_exit)
            content(R.string.kau_exit_confirmation)
            positiveText(R.string.kau_yes)
            negativeText(R.string.kau_no)
            onPositive { _, _ -> finish() }
            checkBoxPromptRes(R.string.kau_do_not_show_again, false, { _, b ->
                Prefs.exitConfirmation = !b
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume {
        menuInflater.inflate(R.menu.menu_main, menu)
        toolbar.tint(Prefs.iconColor)
        setMenuIcons(menu, Prefs.iconColor,
                R.id.action_settings to GoogleMaterial.Icon.gmd_settings
        )
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
        else -> super.onOptionsItemSelected(item)
    }

    private fun loadEvents() {
        doAsync {
            val now = System.currentTimeMillis()
            events = if (isNetworkAvailable) EventRepository.fetch() else emptyList()
            uiThread {
                val later = System.currentTimeMillis()
                val timePassed = later - now
                coordinator.aardvarkSnackbar(String.format(
                        string(R.string.event_loading_time),
                        timePassed
                ))
                tabs.init()
            }
        }
    }

    private fun TabLayout.init() {
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                appBar.setExpanded(true, Prefs.animate)
                val fragment = fragments[tab.position]
                val transaction = supportFragmentManager.beginTransaction()
                if (!fragment.isAdded) transaction.add(R.id.content_main, fragment)
                transaction.show(fragment)
                transaction.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val fragment = fragments[tab.position]
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        AardvarkItem.values().map {
            addTab(newTab().setCustomView(BadgedIcon(this@MainActivity).apply {
                iicon = it.icon
            }))
        }
    }

    private fun setupDrawer(savedInstanceState: Bundle?) {
        val navBg = Prefs.backgroundColor.withMinAlpha(200).toLong()
        val navHeader = Prefs.headerColor.withMinAlpha(200)
        drawer = drawer {
            toolbar = this@MainActivity.toolbar
            savedInstance = savedInstanceState
            translucentStatusBar = false
            sliderBackgroundColor = navBg
            drawerHeader = accountHeader {
                customViewRes = R.layout.material_drawer_header
                textColor = Prefs.iconColor.toLong()
                backgroundDrawable = ColorDrawable(navHeader)
                selectionSecondLineShown = false
                profile(name = Prefs.kervalUser) {
                    icon = R.drawable.aardvark
                    textColor = Prefs.textColor.toLong()
                    selectedTextColor = Prefs.textColor.toLong()
                    selectedColor = 0x00000001.toLong()
                    identifier = Prefs.identifier.toLong()
                }
                profileSetting(nameRes = R.string.kau_logout) {
                    iicon = GoogleMaterial.Icon.gmd_exit_to_app
                    iconColor = Prefs.textColor.toLong()
                    textColor = Prefs.textColor.toLong()
                    identifier = -2L
                }
                profileSetting(nameRes = R.string.kau_add_account) {
                    iconDrawable = IconicsDrawable(
                            this@MainActivity,
                            GoogleMaterial.Icon.gmd_add
                    ).actionBar().paddingDp(5).color(Prefs.textColor)
                    textColor = Prefs.textColor.toLong()
                    identifier = -3L
                }
                profileSetting(nameRes = R.string.kau_manage_account) {
                    iicon = GoogleMaterial.Icon.gmd_settings
                    iconColor = Prefs.textColor.toLong()
                    textColor = Prefs.textColor.toLong()
                    identifier = -4L
                }
                onProfileChanged { _, profile, _ ->
                    when (profile.identifier) {
                        -2L -> materialDialogThemed {
                            title(R.string.kau_logout)
                            content(String.format(
                                    string(R.string.kau_logout_confirm_as_x),
                                    Prefs.kervalUser
                            ))
                            positiveText(R.string.kau_yes)
                            negativeText(R.string.kau_no)
                            onPositive { _, _ -> toast("Logout will be implemented soon") }
                        }
                        -3L -> toast("Login will be implemented soon")
                        -4L -> toast("Profile selector will be implemented soon")
                    }
                    false
                }
            }
            drawerHeader.setActiveProfile(Prefs.identifier.toLong())
            AardvarkItem.values().map {
                primaryAardvarkItem(it)
            }
        }
    }

    private fun Builder.primaryAardvarkItem(item: AardvarkItem) = this.primaryItem(item.titleId) {
        iicon = item.icon
        iconColor = Prefs.textColor.toLong()
        textColor = Prefs.textColor.toLong()
        selectedIconColor = Prefs.textColor.toLong()
        selectedTextColor = Prefs.textColor.toLong()
        selectedColor = 0x00000001.toLong()
        identifier = item.titleId.toLong()
        onClick { _ ->
            aardvarkAnswers {
                logContentView(ContentViewEvent()
                        .putContentName(item.name)
                        .putContentType("drawer_item")
                )
            }
            false
        }
    }
}
