package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.PreferenceSubItems
import de.uni_marburg.mathematik.ds.serval.enums.SupportTopics
import de.uni_marburg.mathematik.ds.serval.settings.*
import de.uni_marburg.mathematik.ds.serval.utils.*

class SettingsActivity : KPrefActivity() {

    private var resultFlag = Activity.RESULT_CANCELED

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSecureFlag()
        setTheme()

        animate = animationsAreEnabled
        themeExterior(animate = false)
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        PreferenceSubItems.values()
            .filter { if (!experimentalSettingsAreEnabled) !it.experimental else true }
            .map { preferenceSubItem ->
                subItems(
                    title = preferenceSubItem.titleRes,
                    itemBuilder = when (preferenceSubItem) {
                        PreferenceSubItems.APPEARANCE -> appearanceItemBuilder()
                        PreferenceSubItems.BEHAVIOUR -> behaviourItemBuilder()
                        PreferenceSubItems.EVENT -> eventItemBuilder()
                        PreferenceSubItems.EXPERIMENTAL -> experimentalItemBuilder()
                        PreferenceSubItems.LOCATION -> locationItemBuilder()
                        PreferenceSubItems.MAP -> mapItemBuilder()
                        PreferenceSubItems.SERVAL -> servalItemBuilder()
                    },
                    builder = {
                        descRes = preferenceSubItem.descRes
                        iicon = preferenceSubItem.iicon
                    }
                )
            }

        plainText(R.string.aardvark_about) {
            descRes = R.string.aardvark_about_desc
            iicon = GoogleMaterial.Icon.gmd_info
            onClick = {
                startActivityForResult<AboutActivity>(
                    requestCode = ACTIVITY_ABOUT,
                    bundleBuilder = {
                        if (animationsAreEnabled) withSceneTransitionAnimation(context)
                    }
                )
            }
        }

        plainText(R.string.preference_replay_intro) {
            iicon = GoogleMaterial.Icon.gmd_replay
            onClick = {
                startActivityForResult<IntroActivity>(
                    requestCode = ACTIVITY_INTRO,
                    bundleBuilder = {
                        if (animationsAreEnabled) {
                            withCustomAnimation(
                                context = context,
                                enterResId = R.anim.kau_slide_in_bottom,
                                exitResId = R.anim.kau_fade_out
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_INTRO -> shouldRestartMain()
            ACTIVITY_ABOUT -> if (resultCode == Activity.RESULT_OK) restart()
        }
    }

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = AppearancePrefs.Theme::accentColor
        textColor = AppearancePrefs.Theme::textColor
    }

    override fun onBackPressed() {
        if (!super.backPress()) {
            setResult(resultFlag)
            finishSlideOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean = createOptionsMenu(
        menuRes = R.menu.menu_settings,
        menu = menu,
        iicons = *arrayOf(
            R.id.action_email to GoogleMaterial.Icon.gmd_email,
            R.id.action_changelog to GoogleMaterial.Icon.gmd_info
        )
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
        when (item.itemId) {
            R.id.action_email -> materialDialogThemed {
                title(R.string.support_email_subject)
                items(SupportTopics.values().map { string(it.titleRes) })
                itemsCallback { _, _, which, _ -> SupportTopics.values()[which].sendEmail(context) }
            }
            R.id.action_changelog -> showChangelog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun shouldRestartMain() = setAardvarkResult(MainActivity.REQUEST_RESTART)

    fun shouldRestartApplication() = setAardvarkResult(MainActivity.REQUEST_APPLICATION_RESTART)

    fun themeExterior(animate: Boolean = animationsAreEnabled) {
        if (animate) {
            bgCanvas.fade(color = AppearancePrefs.Theme.backgroundColor)
            toolbarCanvas.ripple(
                color = AppearancePrefs.Theme.headerColor,
                startX = RippleCanvas.MIDDLE,
                startY = RippleCanvas.END
            )
        } else {
            bgCanvas.set(color = AppearancePrefs.Theme.backgroundColor)
            toolbarCanvas.set(color = AppearancePrefs.Theme.headerColor)
        }
        themeNavigationBar()
    }

    fun setAardvarkResult(flag: Int) {
        resultFlag = resultFlag or flag
    }

    companion object {
        const val ACTIVITY_INTRO = 1 shl 1
        const val ACTIVITY_ABOUT = 1 shl 2
    }

}