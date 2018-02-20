package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.about.kauLaunchAbout
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.ExperimentalPreferenceSubItem
import de.uni_marburg.mathematik.ds.serval.enums.PreferenceSubItem
import de.uni_marburg.mathematik.ds.serval.enums.Support
import de.uni_marburg.mathematik.ds.serval.settings.*
import de.uni_marburg.mathematik.ds.serval.utils.*

class SettingsActivity : KPrefActivity() {

    private var resultFlag = Activity.RESULT_CANCELED

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSecureFlag()
        setTheme()

        animate = Prefs.animate
        themeExterior(animate = false)
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        PreferenceSubItem.values().map { preferenceSubItem ->
            subItems(
                title = preferenceSubItem.titleRes,
                itemBuilder = when (preferenceSubItem) {
                    PreferenceSubItem.BEHAVIOUR -> behaviourItemBuilder()
                    PreferenceSubItem.APPEARANCE -> appearanceItemBuilder()
                    PreferenceSubItem.LOCATION -> locationItemBuilder()
                    PreferenceSubItem.SERVAL -> servalItemBuilder()
                },
                builder = {
                    descRes = preferenceSubItem.descRes
                    iicon = preferenceSubItem.iicon
                }
            )
        }

        plainText(R.string.aardvark_about) {
            descRes = R.string.aardvark_about_description
            iicon = GoogleMaterial.Icon.gmd_info
            onClick = { kauLaunchAbout<AboutActivity>() }
        }
        plainText(R.string.replay_intro) {
            iicon = GoogleMaterial.Icon.gmd_replay
            onClick = { startActivity<IntroActivity>() }
        }

        if (Prefs.debugSettings) {
            header(R.string.experimental)

            ExperimentalPreferenceSubItem.values().map { experimentalPreferenceSubItem ->
                subItems(
                    title = experimentalPreferenceSubItem.titleRes,
                    itemBuilder = when (experimentalPreferenceSubItem) {
                        ExperimentalPreferenceSubItem.DEBUG -> debugItemBuilder()
                    },
                    builder = {
                        descRes = experimentalPreferenceSubItem.descRes
                        iicon = experimentalPreferenceSubItem.iicon
                    }
                )
            }
        }
    }

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = Prefs::accentColor
        textColor = Prefs::textColor
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
                itemsCallback { _, _, which, _ -> Support.values()[which].sendEmail(context) }
            }
            R.id.action_changelog -> aardvarkChangelog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun shouldRestartMain() = setAardvarkResult(MainActivity.REQUEST_RESTART)

    fun shouldRestartApplication() = setAardvarkResult(MainActivity.REQUEST_APPLICATION_RESTART)

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
        themeNavigationBar()
    }

    fun setAardvarkResult(flag: Int) {
        resultFlag = resultFlag or flag
    }

}