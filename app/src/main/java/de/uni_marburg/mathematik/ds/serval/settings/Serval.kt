package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.consume
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

/** Created by thames1990 on 09.12.17. */
fun SettingsActivity.getServalPrefs(): KPrefAdapterBuilder.() -> Unit = {
    header(R.string.serval)
    text(R.string.username, { Prefs.kervalUser }, { Prefs.kervalUser = it }) {
        descRes = R.string.preference_username_description
        onClick = {
            itemView.context.materialDialogThemed {
                title(string(R.string.username))
                input(string(R.string.username), item.pref, { _, input -> item.pref = input.toString() })
            }
        }
    }
    text(R.string.password, { Prefs.kervalPassword }, { Prefs.kervalPassword = it }) {
        descRes = R.string.preference_password_description
        onClick = {
            itemView.context.materialDialogThemed {
                title(string(R.string.password))
                input(string(R.string.password), item.pref, { _, input -> item.pref = input.toString() })
            }
        }
    }
}