package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.DateTimeFormat
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.utils.*

fun SettingsActivity.appearanceItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_appearance_theme_header)

    text(
        title = R.string.preference_appearance_theme,
        getter = Prefs.Appearance::themeIndex,
        setter = { Prefs.Appearance.themeIndex = it },
        builder = {
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_theme)
                    items(Theme.values().map { theme -> string(theme.titleRes) })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            reload()
                            setTheme()
                            themeExterior()
                            invalidateOptionsMenu()
                        }
                        true
                    }
                }
            }
            textGetter = { string(Theme(it).titleRes) }
        }
    )

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = Prefs.Appearance::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
        allowCustom = true
    }

    colorPicker(
        title = R.string.preference_appearance_color_text,
        getter = Prefs.Appearance::customTextColor,
        setter = { customTextColor ->
            Prefs.Appearance.customTextColor = customTextColor
            reload()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    colorPicker(
        title = R.string.preference_appearance_color_accent,
        getter = Prefs.Appearance::customAccentColor,
        setter = { customAccentColor ->
            Prefs.Appearance.customAccentColor = customAccentColor
            reload()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    colorPicker(
        title = R.string.preference_appearance_color_background,
        getter = Prefs.Appearance::customBackgroundColor,
        setter = { customBackgroundColor ->
            Prefs.Appearance.customBackgroundColor = customBackgroundColor
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
        title = R.string.preference_appearance_color_header,
        getter = Prefs.Appearance::customHeaderColor,
        setter = { customHeaderColor ->
            Prefs.Appearance.customHeaderColor = customHeaderColor
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
        title = R.string.preference_appearance_color_icon,
        getter = Prefs.Appearance::customIconColor,
        setter = { customIconColor ->
            Prefs.Appearance.customIconColor = customIconColor
            invalidateOptionsMenu()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    header(R.string.preference_appearance_global_header)

    text(
        title = R.string.preference_appearance_main_activity_layout,
        getter = Prefs.Appearance::mainActivityLayoutIndex,
        setter = { Prefs.Appearance.mainActivityLayoutIndex = it },
        builder = {
            textGetter = { string(Prefs.Appearance.mainActivityLayout.titleRes) }
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_main_activity_layout)
                    items(MainActivityLayout.values().map { mainActivityLayout ->
                        string(mainActivityLayout.titleRes)
                    })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                        }
                        true
                    }
                }
            }
        }
    )

    text(
        title = R.string.preference_appearance_date_time_format,
        getter = Prefs.Appearance::dateTimeFormatIndex,
        setter = { Prefs.Appearance.dateTimeFormatIndex = it },
        builder = {
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_date_time_format)
                    items(DateTimeFormat.values().map { it.previewText })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            reload()
                            shouldRestartMain()
                        }
                        true
                    }
                }
            }
            textGetter = { string(DateTimeFormat(it).titleRes) }
        }
    )

    checkbox(
        title = R.string.preference_appearance_tint_navbar,
        getter = Prefs.Appearance::tintNavBar,
        setter = { tintNavBar ->
            Prefs.Appearance.tintNavBar = tintNavBar
            themeNavigationBar()
            setAardvarkResult(MainActivity.REQUEST_NAV)
        },
        builder = { descRes = R.string.preference_appearance_tint_navbar_desc }
    )

}