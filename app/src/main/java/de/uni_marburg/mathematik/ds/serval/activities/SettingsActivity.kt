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
        if (Prefs.debugSettings) subItems(R.string.debug, getDebugPrefs()) {
            descRes = R.string.debug_description
            iicon = CommunityMaterial.Icon.cmd_android_debug_bridge
        }

        subItems(R.string.behaviour, getBehaviourPrefs()) {
            descRes = R.string.behaviour_description
            iicon = GoogleMaterial.Icon.gmd_settings
        }
        subItems(R.string.appearance, getAppearancePrefs()) {
            descRes = R.string.appearance_description
            iicon = GoogleMaterial.Icon.gmd_palette
        }
        if (Prefs.debugSettings) {
            subItems(R.string.location, getLocationPrefs()) {
                descRes = R.string.location_description
                iicon = GoogleMaterial.Icon.gmd_my_location
            }
        }
        subItems(R.string.serval, getServalPrefs()) {
            descRes = R.string.serval_description
            iicon = GoogleMaterial.Icon.gmd_network_wifi
        }

        plainText(R.string.about_aardvark) {
            descRes = R.string.about_aardvark_description
            iicon = GoogleMaterial.Icon.gmd_info
            onClick = { startActivityForResult(AboutActivity::class.java, 9) }
        }
        plainText(R.string.replay_intro) {
            iicon = GoogleMaterial.Icon.gmd_replay
            onClick = { startActivity(IntroActivity::class.java) }
        }
    }

    fun shouldRestartMain() {
        setAardvarkResult(MainActivity.REQUEST_RESTART)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        setAardvarkTheme()
        super.onCreate(savedInstanceState)
        setSecureFlag()
        setCurrentScreen()
        animate = Prefs.animate
        themeExterior(false)
    }

    fun themeExterior(animate: Boolean = true) {
        if (animate) bgCanvas.fade(Prefs.backgroundColor)
        else bgCanvas.set(Prefs.backgroundColor)
        if (animate) toolbarCanvas.ripple(Prefs.headerColor, RippleCanvas.MIDDLE, RippleCanvas.END)
        else toolbarCanvas.set(Prefs.headerColor)
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
        toolbar.tint(Prefs.iconColor)
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
                items(Support.values().map { string(it.title) })
                itemsCallback { _, _, which, _ ->
                    Support.values()[which].sendEmail(this@SettingsActivity)
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