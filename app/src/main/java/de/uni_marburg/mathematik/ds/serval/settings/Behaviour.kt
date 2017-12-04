package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.view.activities.SettingsActivity
import org.jetbrains.anko.toast

fun SettingsActivity.getBehaviourPrefs(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(R.string.preference_changelog, { Prefs.changelog }, { Prefs.changelog = it }) {
        descRes = R.string.preference_changelog_desc
    }

    checkbox(R.string.preference_confirm_exit, { Prefs.exitConfirmation }, { Prefs.exitConfirmation = it })

    checkbox(R.string.preference_use_secure_flag, { Prefs.useSecureFlag }, { Prefs.useSecureFlag = it }) {
        descRes = R.string.preference_use_secure_flag_description
    }

    checkbox(R.string.preference_use_analytics, { Prefs.analytics }, { useAnalytics ->
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
                toast(string(R.string.preference_reset_analytics_confirmation))
            }
        }
    }) {
        descRes = R.string.preference_use_analytics_description
    }
}