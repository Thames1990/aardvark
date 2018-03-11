package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

fun SettingsActivity.behaviourItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(
        title = R.string.preference_behaviour_animations,
        getter = Prefs.Behaviour::animate,
        setter = { useAnimations ->
            Prefs.Behaviour.animate = useAnimations
            animate = useAnimations
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_behaviour_animations_desc }
    )

    checkbox(
        title = R.string.kau_changelog,
        getter = Prefs.Behaviour::showChangelog,
        setter = { Prefs.Behaviour.showChangelog = it },
        builder = { descRes = R.string.preference_behaviour_changelog_desc }
    )

    checkbox(
        title = R.string.preference_behaviour_confirm_exit,
        getter = Prefs.Behaviour::confirmExit,
        setter = { Prefs.Behaviour.confirmExit = it }
    )

    checkbox(
        title = R.string.preference_behaviour_analytics,
        getter = Prefs.Behaviour::analytics,
        setter = { useAnalytics ->
            Prefs.Behaviour.analytics = useAnalytics
            if (!useAnalytics) materialDialogThemed {
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
            shouldRestartApplication()
        },
        builder = { descRes = R.string.preference_behaviour_analytics_desc }
    )
}