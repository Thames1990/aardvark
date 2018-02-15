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
        getter = Prefs::themeType,
        setter = { Prefs.themeType = it }
    ) {
        onClick = {
            materialDialogThemed {
                title(R.string.theme)
                items(Theme.values().map { theme -> string(theme.titleRes) })
                itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                    if (item.pref != which) {
                        item.pref = which
                        shouldRestartMain()
                        reload()
                        setAardvarkTheme()
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

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = Prefs::isCustomTheme
        onDisabledClick = { aardvarkSnackbar(R.string.requires_custom_theme) }
        allowCustom = true
    }

    colorPicker(
        title = R.string.color_text,
        getter = Prefs::customTextColor,
        setter = { customTextColor ->
            Prefs.customTextColor = customTextColor
            reload()
            shouldRestartMain()
        }
    ) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    colorPicker(
        title = R.string.color_accent,
        getter = Prefs::customAccentColor,
        setter = { customAccentColor ->
            Prefs.customAccentColor = customAccentColor
            reload()
            shouldRestartMain()
        }
    ) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    colorPicker(
        title = R.string.color_background,
        getter = Prefs::customBackgroundColor,
        setter = { customBackgroundColor ->
            Prefs.customBackgroundColor = customBackgroundColor
            bgCanvas.ripple(color = customBackgroundColor, duration = 500L)
            setAardvarkTheme()
            shouldRestartMain()
        }
    ) {
        dependsOnCustom()
        allowCustomAlpha = true
    }

    colorPicker(
        title = R.string.color_header,
        getter = Prefs::customHeaderColor,
        setter = { customHeaderColor ->
            Prefs.customHeaderColor = customHeaderColor
            aardvarkNavigationBar()
            toolbarCanvas.ripple(
                color = customHeaderColor,
                startX = RippleCanvas.MIDDLE,
                startY = RippleCanvas.END,
                duration = 500L
            )
            reload()
            shouldRestartMain()
        }
    ) {
        dependsOnCustom()
        allowCustomAlpha = true
    }

    colorPicker(
        title = R.string.color_icon,
        getter = Prefs::customIconColor,
        setter = { customIconColor ->
            Prefs.customIconColor = customIconColor
            invalidateOptionsMenu()
            shouldRestartMain()
        }
    ) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = Prefs::isCustomTheme
        onDisabledClick = { aardvarkSnackbar(R.string.requires_custom_theme) }
    }

    text(
        title = R.string.maps_style,
        getter = Prefs::mapsStyleType,
        setter = { Prefs.mapsStyleType = it }
    ) {
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
                        setAardvarkTheme()
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

    header(R.string.global_customization)

    text(
        title = R.string.main_activity_layout,
        getter = Prefs::mainActivityLayoutType,
        setter = { Prefs.mainActivityLayoutType = it }
    ) {
        textGetter = { string(Prefs.mainActivityLayout.titleRes) }
        onClick = {
            materialDialogThemed {
                title(R.string.set_main_activity_layout)
                items(MainActivityLayout.values.map { string(it.titleRes) })
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

    checkbox(
        title = R.string.tint_nav,
        getter = Prefs::tintNavBar,
        setter = { tintNavBar ->
            Prefs.tintNavBar = tintNavBar
            aardvarkNavigationBar()
            setAardvarkResult(MainActivity.REQUEST_NAV)
        }
    ) { descRes = R.string.tint_nav_desc }

}