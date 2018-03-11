package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

fun SettingsActivity.servalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    text(
        title = R.string.preference_serval_username,
        getter = Prefs::servalUser,
        setter = { Prefs.servalUser = it },
        builder = {
            descRes = R.string.preference_serval_username_desc
            onClick = {
                materialDialogThemed {
                    title(string(R.string.preference_serval_username))
                    input(
                        string(R.string.preference_serval_username),
                        item.pref,
                        { _, input -> item.pref = input.toString() }
                    )
                }
            }
        }
    )

    text(
        title = R.string.preference_serval_password,
        getter = Prefs::servalPassword,
        setter = { Prefs.servalPassword = it },
        builder = {
            descRes = R.string.preference_serval_password_desc
            onClick = {
                materialDialogThemed {
                    title(string(R.string.preference_serval_password))
                    input(
                        string(R.string.preference_serval_password),
                        item.pref,
                        { _, input -> item.pref = input.toString() }
                    )
                }
            }
        }
    )

    seekbar(
        title = R.string.preference_serval_event_count,
        getter = Prefs::eventCount,
        setter = { Prefs.eventCount = it },
        builder = {
            descRes = R.string.preference_serval_event_count_desc
            min = 1
            max = Prefs.EVENT_COUNT
            shouldRestartMain()
        }
    )
}