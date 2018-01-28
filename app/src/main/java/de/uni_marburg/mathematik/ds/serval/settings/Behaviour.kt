package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.aardvarkSnackbar
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag

fun SettingsActivity.getBehaviourPrefs(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(
        title = R.string.fancy_animations,
        getter = Prefs::animate,
        setter = {
            Prefs.animate = it
            animate = it
            shouldRestartMain()
        }
    ) { descRes = R.string.fancy_animations_description }

    checkbox(
        title = R.string.changelog,
        getter = Prefs::changelog,
        setter = { Prefs.changelog = it }
    ) { descRes = R.string.changelog_description }

    checkbox(
        title = R.string.preference_confirm_exit,
        getter = Prefs::exitConfirmation,
        setter = { Prefs.exitConfirmation = it }
    )

    checkbox(
        title = R.string.secure_app,
        getter = Prefs::secure_app,
        setter = {
            Prefs.secure_app = it
            setSecureFlag()
            shouldRestartApplication()
        }
    ) { descRes = R.string.secure_app_desc }

    checkbox(
        title = R.string.preference_use_analytics,
        getter = Prefs::analytics,
        setter = { useAnalytics ->
            Prefs.analytics = useAnalytics
            if (!useAnalytics) materialDialogThemed {
                title(string(R.string.preference_reset_analytics))
                content(string(R.string.preference_reset_analytics_description))
                positiveText(string(R.string.kau_yes))
                negativeText(string(R.string.kau_no))
                onPositive { _, _ ->
                    Aardvark.firebaseAnalytics.resetAnalyticsData()
                    Prefs.installDate = -1L
                    Prefs.identifier = -1
                    aardvarkSnackbar(R.string.preference_reset_analytics_confirmation)
                }
            }
        }) { descRes = R.string.analytics_description }
}