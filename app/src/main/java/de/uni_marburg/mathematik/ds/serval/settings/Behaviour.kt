package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import com.google.firebase.analytics.FirebaseAnalytics
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

fun SettingsActivity.behaviourItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    checkbox(
        title = R.string.preference_animations,
        getter = Prefs::animate,
        setter = { useAnimations ->
            Prefs.animate = useAnimations
            animate = useAnimations
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_animations_desc }
    )

    checkbox(
        title = R.string.preference_changelog,
        getter = Prefs::changelog,
        setter = { Prefs.changelog = it },
        builder = { descRes = R.string.preference_changelog_desc }
    )

    checkbox(
        title = R.string.preference_confirm_exit,
        getter = Prefs::exitConfirmation,
        setter = { Prefs.exitConfirmation = it }
    )

    checkbox(
        title = R.string.preference_use_analytics,
        getter = Prefs::analytics,
        setter = { useAnalytics ->
            Prefs.analytics = useAnalytics
            if (!useAnalytics) materialDialogThemed {
                title(string(R.string.preference_reset_analytics))
                content(string(R.string.preference_reset_analytics_desc))
                positiveText(string(R.string.kau_yes))
                negativeText(string(R.string.kau_no))
                onPositive { _, _ ->
                    FirebaseAnalytics.getInstance(context).apply {
                        setAnalyticsCollectionEnabled(useAnalytics)
                    }
                    Prefs.installDate = -1L
                    snackbarThemed(R.string.preference_reset_analytics_confirmation)
                }
            }
            shouldRestartApplication()
        },
        builder = { descRes = R.string.preference_analytics_desc }
    )
}