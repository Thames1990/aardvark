package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.arch.lifecycle.LifecycleObserver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
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
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.util.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), LifecycleObserver {

    val toolbar: Toolbar by bindView(R.id.toolbar)
    lateinit var drawer: Drawer
    lateinit var drawerHeader: AccountHeader

    companion object {
        const val ACTIVITY_SETTINGS = 97
        const val REQUEST_RESTART_APPLICATION = 1 shl 1
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
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)
            setupDrawer(savedInstanceState)
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_SETTINGS  -> {
                // Completely restart application
                if (resultCode and REQUEST_RESTART_APPLICATION > 0) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    val pending = PendingIntent.getActivity(
                            this,
                            666,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    )
                    val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (buildIsMarshmallowAndUp) {
                        alarm.setExactAndAllowWhileIdle(
                                AlarmManager.RTC,
                                System.currentTimeMillis() + 100,
                                pending
                        )
                    } else alarm.setExact(
                            AlarmManager.RTC,
                            System.currentTimeMillis() + 100,
                            pending
                    )
                    finish()
                    System.exit(0)
                    return
                }
                if (resultCode and REQUEST_RESTART > 0 && data != null) return restart()
                if (resultCode and REQUEST_NAV > 0) aardvarkNavigationBar()
            }
            INTRO_REQUEST_CODE -> start()
        }
    }

    override fun onBackPressed() {
        if (Prefs.exitConfirmation) materialDialogThemed {
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
        else                 -> super.onOptionsItemSelected(item)
    }

    private fun start() {
        setSecureFlag()
        setCurrentScreen()
        doAsync {
            events = if (isNetworkAvailable) EventRepository.fetch() else emptyList()
            uiThread {
                setContentView(R.layout.activity_main)
            }
        }
    }

    private fun setupDrawer(savedInstanceState: Bundle?) {
        val navBg = Prefs.bgColor.withMinAlpha(200).toLong()
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
                profile(name = BuildConfig.APPLICATION_ID) {
                    textColor = Prefs.textColor.toLong()
                    selectedTextColor = Prefs.textColor.toLong()
                    selectedColor = 0x00000001.toLong()
                    identifier = 1L
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
            }
            drawerHeader.setActiveProfile(1L)
            primaryAardvarkItem(AardvarkItem.DASHBOARD)
            primaryAardvarkItem(AardvarkItem.EVENTS)
            primaryAardvarkItem(AardvarkItem.MAP)
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
            de.uni_marburg.mathematik.ds.serval.util.aardvarkAnswers {
                logContentView(ContentViewEvent()
                        .putContentName(item.name)
                        .putContentType("drawer_item")
                )
            }
            false
        }
    }
}
