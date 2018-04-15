package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toast
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

object ServalPrefs : KPref() {
    const val EVENT_COUNT = 10000

    var baseUrl: String by kpref(key = "SERVAL_BASE_URL", fallback = BuildConfig.SERVAL_BASE_URL)
    var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    var password: String by kpref(key = "SERVAL_PASSWORD", fallback = BuildConfig.SERVAL_PASSWORD)
    var port: Int by kpref(key = "SERVAL_PORT", fallback = BuildConfig.SERVAL_PORT)
    var user: String by kpref(key = "SERVAL_USER", fallback = BuildConfig.SERVAL_USER)
}

fun SettingsActivity.servalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    fun showUserNameDialog(onClick: KClick<String>) {
        materialDialogThemed {
            title(string(R.string.preference_serval_username))
            with(onClick) {
                input(string(R.string.preference_serval_username), item.pref, { _, input ->
                    item.pref = input.toString()
                })
            }
        }
    }

    text(
        title = R.string.preference_serval_username,
        getter = ServalPrefs::user,
        setter = { ServalPrefs.user = it },
        builder = {
            descRes = R.string.preference_serval_username_desc
            onClick = ::showUserNameDialog
        }
    )

    fun showPasswordDialog(onClick: KClick<String>) {
        materialDialogThemed {
            title(string(R.string.preference_serval_password))
            with(onClick) {
                input(string(R.string.preference_serval_password), item.pref, { _, input ->
                    item.pref = input.toString()
                })
            }
        }
    }

    text(
        title = R.string.preference_serval_password,
        getter = ServalPrefs::password,
        setter = { ServalPrefs.password = it },
        builder = {
            descRes = R.string.preference_serval_password_desc
            onClick = ::showPasswordDialog
        }
    )

    fun shouldReloadEvents() = setAardvarkResult(MainActivity.REQUEST_RELOAD_EVENTS)

    seekbar(
        title = R.string.preference_serval_event_count,
        getter = ServalPrefs::eventCount,
        setter = {
            ServalPrefs.eventCount = it
            toast(string(R.string.preference_serval_event_reload_toast_message))
        },
        builder = {
            descRes = R.string.preference_serval_event_count_desc
            min = 1
            max = ServalPrefs.EVENT_COUNT
            shouldReloadEvents()
        }
    )

}