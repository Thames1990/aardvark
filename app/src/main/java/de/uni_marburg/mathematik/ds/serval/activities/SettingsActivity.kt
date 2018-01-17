package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Support
import de.uni_marburg.mathematik.ds.serval.settings.*
import de.uni_marburg.mathematik.ds.serval.utils.*

class SettingsActivity : KPrefActivity() {

    private var resultFlag = Activity.RESULT_CANCELED

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = { Prefs.accentColor }
        textColor = { Prefs.textColor }
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        subItems(R.string.behaviour, getBehaviourPrefs()) {
            descRes = R.string.behaviour_description
            iicon = GoogleMaterial.Icon.gmd_settings
        }
        subItems(R.string.appearance, getAppearancePrefs()) {
            descRes = R.string.appearance_description
            iicon = GoogleMaterial.Icon.gmd_palette
        }
        subItems(R.string.serval, getServalPrefs()) {
            descRes = R.string.serval_description
            iicon = GoogleMaterial.Icon.gmd_network_wifi
        }

        plainText(R.string.aardvark_about) {
            descRes = R.string.aardvark_about_description
            iicon = GoogleMaterial.Icon.gmd_info
            onClick = { startActivityForResult(AboutActivity::class.java, 9) }
        }
        plainText(R.string.replay_intro) {
            iicon = GoogleMaterial.Icon.gmd_replay
            onClick = { startActivity(IntroActivity::class.java) }
        }
        if (Prefs.debugSettings) {
            header(R.string.experimental)
            subItems(R.string.debug, getDebugPrefs()) {
                descRes = R.string.debug_description
                iicon = CommunityMaterial.Icon.cmd_android_debug_bridge
            }
            subItems(R.string.location, getLocationPrefs()) {
                descRes = R.string.location_description
                iicon = GoogleMaterial.Icon.gmd_my_location
            }
        }
    }

    fun shouldRestartMain() {
        setAardvarkResult(MainActivity.REQUEST_RESTART)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentScreen()
        setSecureFlag()
        setAardvarkTheme()
        animate = Prefs.animate
        themeExterior(animate = false)
    }

    fun themeExterior(animate: Boolean = Prefs.animate) {
        if (animate) {
            bgCanvas.fade(color = Prefs.backgroundColor)
            toolbarCanvas.ripple(
                color = Prefs.headerColor,
                startX = RippleCanvas.MIDDLE,
                startY = RippleCanvas.END
            )
        } else {
            bgCanvas.set(color = Prefs.backgroundColor)
            toolbarCanvas.set(color = Prefs.headerColor)
        }
        aardvarkNavigationBar()
    }

    override fun onBackPressed() {
        if (!super.backPress()) {
            setResult(resultFlag)
            finishSlideOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        toolbar.tint(color = Prefs.iconColor)
        setMenuIcons(
            menu = menu,
            color = Prefs.iconColor,
            iicons = *arrayOf(
                R.id.action_email to GoogleMaterial.Icon.gmd_email,
                R.id.action_changelog to GoogleMaterial.Icon.gmd_info
            )
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_email -> materialDialogThemed {
                title(R.string.subject)
                items(Support.values().map { string(it.titleRes) })
                itemsCallback { _, _, which, _ ->
                    Support.values()[which].sendEmail(context)
                }
            }
            R.id.action_changelog -> aardvarkChangelog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun setAardvarkResult(flag: Int) {
        resultFlag = resultFlag or flag
    }

}