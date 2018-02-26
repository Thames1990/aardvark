package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.MapsStyle
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.utils.*

fun SettingsActivity.appearanceItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.theme_customization)

    text(
        title = R.string.theme,
        getter = Prefs::themeIndex,
        setter = { Prefs.themeIndex = it },
        builder = {
            onClick = {
                materialDialogThemed {
                    title(R.string.theme)
                    items(Theme.values().map { theme -> string(theme.titleRes) })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            reload()
                            setTheme()
                            themeExterior()
                            invalidateOptionsMenu()
                            aardvarkAnswersCustom(
                                name = "Theme",
                                events = *arrayOf("Count" to Theme(which).name)
                            )
                        }
                        true
                    }
                }
            }
            textGetter = { string(Theme(it).titleRes) }
        }
    )

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = Prefs::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
        allowCustom = true
    }

    colorPicker(
        title = R.string.color_text,
        getter = Prefs::customTextColor,
        setter = { customTextColor ->
            Prefs.customTextColor = customTextColor
            reload()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    colorPicker(
        title = R.string.color_accent,
        getter = Prefs::customAccentColor,
        setter = { customAccentColor ->
            Prefs.customAccentColor = customAccentColor
            reload()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    colorPicker(
        title = R.string.color_background,
        getter = Prefs::customBackgroundColor,
        setter = { customBackgroundColor ->
            Prefs.customBackgroundColor = customBackgroundColor
            bgCanvas.ripple(color = customBackgroundColor, duration = 500L)
            setTheme()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = true
        }
    )

    colorPicker(
        title = R.string.color_header,
        getter = Prefs::customHeaderColor,
        setter = { customHeaderColor ->
            Prefs.customHeaderColor = customHeaderColor
            themeNavigationBar()
            toolbarCanvas.ripple(
                color = customHeaderColor,
                startX = RippleCanvas.MIDDLE,
                startY = RippleCanvas.END,
                duration = 500L
            )
            reload()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = true
        }
    )

    colorPicker(
        title = R.string.color_icon,
        getter = Prefs::customIconColor,
        setter = { customIconColor ->
            Prefs.customIconColor = customIconColor
            invalidateOptionsMenu()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = Prefs::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
    }

    text(
        title = R.string.maps_style,
        getter = Prefs::mapsStyleIndex,
        setter = { Prefs.mapsStyleIndex = it },
        builder = {
            dependsOnCustom()
            onClick = {
                materialDialogThemed {
                    title(R.string.maps_style)
                    items(MapsStyle.values().map { mapsStyle -> string(mapsStyle.titleRes) })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            reload()
                            setTheme()
                            themeExterior()
                            invalidateOptionsMenu()
                            aardvarkAnswersCustom(
                                name = "Maps style",
                                events = *arrayOf("Count" to MapsStyle(which).name)
                            )
                        }
                        true
                    }
                }
            }
            textGetter = { string(MapsStyle(it).titleRes) }
        }
    )

    header(R.string.preference_global_customization)

    text(
        title = R.string.preference_main_activity_layout,
        getter = Prefs::mainActivityLayoutIndex,
        setter = { Prefs.mainActivityLayoutIndex = it },
        builder = {
            textGetter = { string(Prefs.mainActivityLayout.titleRes) }
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_set_main_activity_layout)
                    items(MainActivityLayout.values().map { mainActivityLayout ->
                        string(mainActivityLayout.titleRes)
                    })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            aardvarkAnswersCustom(
                                name = "Main Layout",
                                events = *arrayOf("Type" to MainActivityLayout(which).name)
                            )
                        }
                        true
                    }
                }
            }
        }
    )

    checkbox(
        title = R.string.preference_tint_nav,
        getter = Prefs::tintNavBar,
        setter = { tintNavBar ->
            Prefs.tintNavBar = tintNavBar
            themeNavigationBar()
            setAardvarkResult(MainActivity.REQUEST_NAV)
        },
        builder = { descRes = R.string.preference_tint_nav_desc }
    )

}