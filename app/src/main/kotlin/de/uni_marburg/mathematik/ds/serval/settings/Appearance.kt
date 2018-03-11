package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.DateTimeFormats
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayouts
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.utils.*

fun SettingsActivity.appearanceItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_appearance_theme_header)

    text(
        title = R.string.preference_appearance_theme,
        getter = Prefs.Appearance.Theme::index,
        setter = { Prefs.Appearance.Theme.index = it },
        builder = {
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_theme)
                    items(Themes.values().map { theme -> string(theme.titleRes) })
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
            textGetter = { string(Themes(it).titleRes) }
        }
    )

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = Prefs.Appearance.Theme::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
        allowCustom = true
    }

    colorPicker(
        title = R.string.preference_appearance_color_text,
        getter = Prefs.Appearance.Theme::customTextColor,
        setter = { customTextColor ->
            Prefs.Appearance.Theme.customTextColor = customTextColor
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
        getter = Prefs.Appearance.Theme::customAccentColor,
        setter = { customAccentColor ->
            Prefs.Appearance.Theme.customAccentColor = customAccentColor
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
        getter = Prefs.Appearance.Theme::customBackgroundColor,
        setter = { customBackgroundColor ->
            Prefs.Appearance.Theme.customBackgroundColor = customBackgroundColor
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
        getter = Prefs.Appearance.Theme::customHeaderColor,
        setter = { customHeaderColor ->
            Prefs.Appearance.Theme.customHeaderColor = customHeaderColor
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
        getter = Prefs.Appearance.Theme::customIconColor,
        setter = { customIconColor ->
            Prefs.Appearance.Theme.customIconColor = customIconColor
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
        getter = Prefs.Appearance.MainActivityLayout::index,
        setter = { Prefs.Appearance.MainActivityLayout.index = it },
        builder = {
            textGetter = { string(Prefs.Appearance.MainActivityLayout.titleRes) }
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_main_activity_layout)
                    items(MainActivityLayouts.values().map { mainActivityLayout ->
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
        getter = Prefs.Appearance.DateTimeFormat::index,
        setter = { Prefs.Appearance.DateTimeFormat.index = it },
        builder = {
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_appearance_date_time_format)
                    items(DateTimeFormats.values().map { it.previewText })
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
            textGetter = { string(DateTimeFormats(it).titleRes) }
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