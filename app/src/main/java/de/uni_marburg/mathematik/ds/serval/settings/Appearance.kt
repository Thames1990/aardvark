package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.snackbar
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.Theme
import de.uni_marburg.mathematik.ds.serval.utils.*

fun SettingsActivity.getAppearancePrefs(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.theme_customization)

    text(R.string.theme, { Prefs.theme }, { Prefs.theme = it }) {
        onClick = { _, _, item ->
            consume {
                materialDialogThemed {
                    title(R.string.theme)
                    items(Theme.values()
                            .map { it.textRes }
                            .map { string(it) }
                    )
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        consumeIf(item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            reload()
                            setAardvarkTheme()
                            themeExterior()
                            invalidateOptionsMenu()
                        }
                    }
                }
            }
        }
        textGetter = {
            string(Theme(it).textRes)
        }
    }

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = { Prefs.isCustomTheme }
        onDisabledClick = { _, _, _ -> consume { snackbar(R.string.requires_custom_theme) } }
        allowCustom = true
    }

    colorPicker(R.string.color_text, { Prefs.customTextColor }, {
        Prefs.customTextColor = it
        reload()
        shouldRestartMain()
    }) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    colorPicker(R.string.color_accent, { Prefs.customAccentColor }, {
        Prefs.customAccentColor = it
        reload()
        shouldRestartMain()
    }) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    colorPicker(R.string.color_background, { Prefs.customBackgroundColor }, {
        Prefs.customBackgroundColor = it
        bgCanvas.ripple(it, duration = 500L)
        setAardvarkTheme()
        shouldRestartMain()
    }) {
        dependsOnCustom()
        allowCustomAlpha = true
    }

    colorPicker(R.string.header_color, { Prefs.customHeaderColor }, {
        Prefs.customHeaderColor = it
        aardvarkNavigationBar()
        toolbarCanvas.ripple(it, RippleCanvas.MIDDLE, RippleCanvas.END, duration = 500L)
        reload()
        shouldRestartMain()
    }) {
        dependsOnCustom()
        allowCustomAlpha = true
    }

    colorPicker(R.string.icon_color, { Prefs.customIconColor }, {
        Prefs.customIconColor = it
        invalidateOptionsMenu()
        shouldRestartMain()
    }) {
        dependsOnCustom()
        allowCustomAlpha = false
    }

    colorPicker(R.string.color_primary, { Prefs.colorPrimary }, {
        Prefs.colorPrimary = it
        reload()
        toolbarCanvas.ripple(it, RippleCanvas.MIDDLE, RippleCanvas.END, 500L)
    })

    header(R.string.global_customization)

    checkbox(R.string.tint_nav, { Prefs.tintNavBar }, {
        Prefs.tintNavBar = it
        aardvarkNavigationBar()
        setAardvarkResult(MainActivity.REQUEST_NAV)
    }) {
        descRes = R.string.tint_nav_desc
    }

}