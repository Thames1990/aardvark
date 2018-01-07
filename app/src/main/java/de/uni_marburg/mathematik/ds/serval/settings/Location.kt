package de.uni_marburg.mathematik.ds.serval.settings

import android.view.View
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import com.afollestad.materialdialogs.MaterialDialog
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriority
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.consume
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

/**
 * Created by thames1990 on 07.01.18.
 */
fun SettingsActivity.getLocationPrefs(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.location)

    plainText(R.string.location_dependency_warning)

    // TODO Fix min/max dependency

    seekbar(
            R.string.location_request_interval,
            { Prefs.locationRequestInterval },
            { Prefs.locationRequestInterval = it }
    ) {
        descRes = R.string.location_request_interval_description
        min = Prefs.locationRequestFastestInterval
    }

    seekbar(
            R.string.location_request_fastest_interval,
            { Prefs.locationRequestFastestInterval },
            { Prefs.locationRequestFastestInterval = it }
    ) {
        descRes = R.string.location_request_fastest_interval_description
        min = 1
        max = Prefs.locationRequestInterval
    }

    // TODO Refactor to the way I implemented it in Appearance with theme
    plainText(R.string.location_request_priority) {
        onClick = {
            itemView.context.materialDialogThemed {
                title(R.string.location_request_priority)
                items(LocationRequestPriority.values().map {
                    "${string(it.resId)}\n${string(it.descriptionResId)}"
                })
                itemsCallbackSingleChoice(Prefs.locationRequestPriorityDialogIndex) { _, _, which, _ ->
                    consume {
                        Prefs.locationRequestPriorityDialogIndex = which
                        Prefs.locationRequestPriority = LocationRequestPriority.values()[which].priority
                    }
                }
            }
        }
    }
}