package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.isReleaseBuild
import de.uni_marburg.mathematik.ds.serval.utils.logAnalytics
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

object BehaviourPrefs : KPref() {
    var analyticsEnabled: Boolean by kpref(key = "ANALYTICS_ENABLED", fallback = isReleaseBuild)
    var animationsEnabled: Boolean by kpref(
        key = "ANIMATIONS_ENABLED",
        fallback = true,
        postSetter = { logAnalytics(name = "Animations enabled", events = *arrayOf("Count" to it)) }
    )
    var confirmExit: Boolean by kpref(key = "CONFIRM_EXIT", fallback = isReleaseBuild)
    var showChangelog: Boolean by kpref(key = "SHOW_CHANGELOG", fallback = isReleaseBuild)
}

fun SettingsActivity.behaviourItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(
        title = R.string.preference_behaviour_animations,
        getter = BehaviourPrefs::animationsEnabled,
        setter = { animationsEnabled ->
            BehaviourPrefs.animationsEnabled = animationsEnabled
            animate = animationsEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_behaviour_animations_desc }
    )

    checkbox(
        title = R.string.kau_changelog,
        getter = BehaviourPrefs::showChangelog,
        setter = { BehaviourPrefs.showChangelog = it },
        builder = { descRes = R.string.preference_behaviour_changelog_desc }
    )

    checkbox(
        title = R.string.preference_behaviour_confirm_exit,
        getter = BehaviourPrefs::confirmExit,
        setter = { BehaviourPrefs.confirmExit = it }
    )

    fun showResetAnalyticsDialog() {
        materialDialogThemed {
            title(string(R.string.preference_behaviour_reset_analytics))
            content(string(R.string.preference_behaviour_reset_analytics_desc))
            positiveText(string(R.string.kau_yes))
            negativeText(string(R.string.kau_no))
            onPositive { _, _ ->
                Aardvark.firebaseAnalytics.resetAnalyticsData()
                Prefs.installDate = -1L
                snackbarThemed(R.string.preference_behaviour_reset_analytics_confirmation)
            }
        }
    }

    checkbox(
        title = R.string.preference_behaviour_analytics,
        getter = BehaviourPrefs::analyticsEnabled,
        setter = { analyticsEnabled ->
            BehaviourPrefs.analyticsEnabled = analyticsEnabled
            if (!analyticsEnabled) showResetAnalyticsDialog()
            shouldRestartApplication()
        },
        builder = { descRes = R.string.preference_behaviour_analytics_desc }
    )

}