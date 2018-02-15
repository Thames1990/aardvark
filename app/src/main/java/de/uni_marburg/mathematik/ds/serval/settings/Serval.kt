package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

fun SettingsActivity.servalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {
    // Serval API username
    text(
        title = R.string.username,
        getter = Prefs::kervalUser,
        setter = { Prefs.kervalUser = it },
        builder = {
            descRes = R.string.preference_username_description
            onClick = {
                materialDialogThemed {
                    title(string(R.string.username))
                    input(
                        string(R.string.username),
                        item.pref,
                        { _, input -> item.pref = input.toString() }
                    )
                }
            }
        }
    )

    // Serval API password
    text(
        title = R.string.password,
        getter = Prefs::kervalPassword,
        setter = { Prefs.kervalPassword = it },
        builder = {
            descRes = R.string.preference_password_description
            onClick = {
                materialDialogThemed {
                    title(string(R.string.password))
                    input(
                        string(R.string.password),
                        item.pref,
                        { _, input -> item.pref = input.toString() }
                    )
                }
            }
        }
    )

    // Events to fetch from Serval API
    seekbar(
        title = R.string.event_count,
        getter = Prefs::eventCount,
        setter = { Prefs.eventCount = it },
        builder = {
            descRes = R.string.event_count_description
            min = 1
            max = Prefs.EVENT_COUNT
            shouldRestartMain()
        }
    )
}