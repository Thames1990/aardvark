package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.RadiationUnits
import de.uni_marburg.mathematik.ds.serval.enums.TemperatureUnits
import de.uni_marburg.mathematik.ds.serval.utils.logAnalytics
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

object EventPrefs : KPref() {

    object RadiationUnit {
        var index: Int by kpref(
            key = "RADIATION_INDEX",
            fallback = RadiationUnits.MICROSIEVERT.ordinal,
            postSetter = {
                loader.invalidate()
                logAnalytics(
                    name = "Radiation Unit",
                    events = *arrayOf("Count" to RadiationUnits(it).name)
                )
            }
        )
        private val loader = lazyResettable { RadiationUnits.values()[index] }
        val unit: RadiationUnits by loader
    }

    object TemperatureUnit {
        var index: Int by kpref(
            key = "TEMPERATURE_INDEX",
            fallback = TemperatureUnits.CELSIUS.ordinal,
            postSetter = {
                loader.invalidate()
                logAnalytics(
                    name = "Temperature Unit",
                    events = *arrayOf("Count" to TemperatureUnits(it).name)
                )
            }
        )
        private val loader = lazyResettable { TemperatureUnits.values()[index] }
        val unit: TemperatureUnits by loader
    }

}

fun SettingsActivity.eventItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    fun showRadiationUnitChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_event_radiation_unit)
            items(RadiationUnits.values().map { string(it.titleRes) })
            with(onClick) {
                itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                    if (item.pref != which) {
                        item.pref = which
                        shouldRestartMain()
                        reload()
                    }
                    true
                }
            }
        }
    }

    text(
        title = R.string.preference_event_radiation_unit,
        getter = EventPrefs.RadiationUnit::index,
        setter = { EventPrefs.RadiationUnit.index = it },
        builder = {
            onClick = ::showRadiationUnitChooserDialog
            textGetter = { string(RadiationUnits(it).titleRes) }
        }
    )

    fun showTemperatureUnitChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_event_temperature_unit)
            items(TemperatureUnits.values().map { string(it.titleRes) })
            with(onClick) {
                itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                    if (item.pref != which) {
                        item.pref = which
                        shouldRestartMain()
                        reload()
                    }
                    true
                }
            }
        }
    }

    text(
        title = R.string.preference_event_temperature_unit,
        getter = EventPrefs.TemperatureUnit::index,
        setter = { EventPrefs.TemperatureUnit.index = it },
        builder = {
            onClick = ::showTemperatureUnitChooserDialog
            textGetter = { string(TemperatureUnits(it).titleRes) }
        }
    )

}