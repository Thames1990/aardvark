package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.AardvarkItem
import de.uni_marburg.mathematik.ds.serval.fragments.DashboardFragment
import de.uni_marburg.mathematik.ds.serval.fragments.EventsFragment
import de.uni_marburg.mathematik.ds.serval.fragments.MapFragment
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.BadgedIcon
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : BaseActivity() {

    private lateinit var eventBadgedIcon: BadgedIcon
    private lateinit var eventViewModel: EventViewModel

    private val appBar: AppBarLayout by bindView(R.id.appbar)
    private val tabs: TabLayout by bindView(R.id.tabs)
    private val toolbar: Toolbar by bindView(R.id.toolbar)

    private val fragments = listOf(
        DashboardFragment(),
        EventsFragment(),
        MapFragment()
    )

    companion object {
        const val ACTIVITY_SETTINGS = 1 shl 1
        const val REQUEST_RESTART = 1 shl 2
        const val REQUEST_NAV = 1 shl 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventViewModel = ViewModelProviders.of(this).get(EventViewModel::class.java)

        setContentView(Prefs.mainActivityLayout.layoutRes)
        setSupportActionBar(toolbar)

        setAardvarkColors {
            toolbar(toolbar)
            themeWindow = false
            header(appBar)
        }

        tabs.setup()

        checkForNewVersion()
        eventViewModel.events.observe(this, Observer { tabs.reloadTabs() })
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

    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> startActivityForResult(
                clazz = SettingsActivity::class.java,
                requestCode = ACTIVITY_SETTINGS,
                bundleBuilder = {
                    withCustomAnimation(
                        context = this@MainActivity,
                        enterResId = R.anim.kau_slide_in_right,
                        exitResId = R.anim.kau_fade_out
                    )
                }
            )
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun TabLayout.setup() {
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                addFragmentSafely(
                    fragment = fragments[tab.position],
                    tag = tab.position.toString(),
                    containerViewId = R.id.content_main
                )
                appBar.setExpanded(true, Prefs.animate)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val fragment = fragments[tab.position]
                supportFragmentManager.beginTransaction().hide(fragment).commit()
            }

            override fun onTabReselected(tab: TabLayout.Tab) = eventViewModel.reload()
        })
        setBackgroundColor(Prefs.mainActivityLayout.backgroundColor)
        loadTabs()
    }

    private fun TabLayout.loadTabs() {
        AardvarkItem.values().map { aardvarkItem ->
            when (aardvarkItem) {
                AardvarkItem.EVENTS -> {
                    eventBadgedIcon = BadgedIcon(context).apply {
                        title = string(aardvarkItem.titleRes)
                        iicon = aardvarkItem.iicon
                        doAsync {
                            val eventCount: Int = eventViewModel.dao.count()
                            uiThread { badgeText = eventCount.toString() }
                        }
                    }
                    addTab(newTab().setCustomView(eventBadgedIcon))
                }
                else -> addTab(newTab().setCustomView(BadgedIcon(context).apply {
                    iicon = aardvarkItem.iicon
                }))
            }
        }
    }

    private fun TabLayout.reloadTabs() {
        doAsync {
            val eventCount: Int = eventViewModel.dao.count()
            uiThread { eventBadgedIcon.badgeText = eventCount.toString() }
        }
    }

    private fun checkForNewVersion() {
        if (BuildConfig.VERSION_CODE > Prefs.versionCode) {
            Prefs.versionCode = BuildConfig.VERSION_CODE
            if (!BuildConfig.DEBUG) {
                aardvarkChangelog()
                aardvarkAnswersCustom(
                    name = "Version",
                    events = *arrayOf(
                        "Version code" to BuildConfig.VERSION_CODE,
                        "Version name" to BuildConfig.VERSION_NAME,
                        "Build type" to BuildConfig.BUILD_TYPE,
                        "Aardvark id" to Prefs.aardvarkId
                    )
                )
            }
        }
    }
}
