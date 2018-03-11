package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

fun SettingsActivity.servalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    text(
        title = R.string.preference_serval_username,
        getter = Prefs.Serval::user,
        setter = { Prefs.Serval.user = it },
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
        getter = Prefs.Serval::password,
        setter = { Prefs.Serval.password = it },
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

    fun shouldReloadEvents() = setAardvarkResult(MainActivity.RELOAD_EVENTS)

    seekbar(
        title = R.string.preference_serval_event_count,
        getter = Prefs.Serval::eventCount,
        setter = { Prefs.Serval.eventCount = it },
        builder = {
            descRes = R.string.preference_serval_event_count_desc
            min = 1
            max = Prefs.Serval.EVENT_COUNT
            shouldReloadEvents()
        }
    )
}