package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefColorPicker
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.DateTimeFormats
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayouts
import de.uni_marburg.mathematik.ds.serval.enums.Themes
import de.uni_marburg.mathematik.ds.serval.utils.*

object AppearancePrefs : KPref() {

    var tintNavBar: Boolean by kpref(key = "TINT_NAV_BAR", fallback = false)

    object Theme {
        var index: Int by kpref(
            key = "THEME_INDEX",
            fallback = Themes.LIGHT.ordinal,
            postSetter = {
                loader.invalidate()
                logAnalytics(name = "Theme", events = *arrayOf("Count" to Themes(it).name))
            }
        )
        private val loader = lazyResettable { Themes.values()[index] }
        val theme: Themes by loader

        val accentColor: Int
            get() = theme.accentColor
        val bgColor: Int
            get() = theme.bgColor
        val headerColor: Int
            get() = theme.headerColor
        val iconColor: Int
            get() = theme.iconColor
        val isCustomTheme: Boolean
            get() = theme == Themes.CUSTOM
        val textColor: Int
            get() = theme.textColor

        object Custom {
            var accentColor: Int by kpref(key = "COLOR_ACCENT", fallback = Themes.LOCHMARA)
            var bgColor: Int by kpref(key = "COLOR_BACKGROUND", fallback = Themes.MINE_SHAFT)
            var headerColor: Int by kpref(key = "COLOR_HEADER", fallback = Themes.BAHAMA_BLUE)
            var iconColor: Int by kpref(key = "COLOR_ICONS", fallback = Themes.PORCELAIN)
            var textColor: Int by kpref(key = "COLOR_TEXT", fallback = Themes.PORCELAIN)
        }
    }

    object DateTimeFormat {
        var index: Int by kpref(
            key = "DATE_TIME_FORMAT_INDEX",
            fallback = DateTimeFormats.MEDIUM_DATE_MEDIUM_TIME.ordinal,
            postSetter = {
                loader.invalidate()
                logAnalytics(
                    name = "Date time format",
                    events = *arrayOf("Count" to DateTimeFormats(it).name)
                )
            }
        )
        private val loader = lazyResettable { DateTimeFormats.values()[index] }
        val format: DateTimeFormats by loader
    }

    object MainActivityLayout {
        var index: Int by kpref(
            key = "MAIN_ACTIVITY_LAYOUT_INDEX",
            fallback = MainActivityLayouts.TOP_BAR.ordinal,
            postSetter = {
                loader.invalidate()
                logAnalytics(
                    name = "Main Layout",
                    events = *arrayOf("Count" to MainActivityLayouts(it).name)
                )
            }
        )
        private val loader = lazyResettable { MainActivityLayouts.values()[index] }
        val layout: MainActivityLayouts by loader

        val bgColor: Int
            get() = layout.bgColor
        val iconColor: Int
            get() = layout.iconColor
        val layoutRes: Int
            get() = layout.layoutRes
        val titleRes: Int
            get() = layout.titleRes
    }

}

fun SettingsActivity.appearanceItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(title = R.string.preference_appearance_theme_header)

    fun showThemeChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_appearance_theme)
            items(Themes.values().map { theme -> string(theme.titleRes) })
            with(onClick) {
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
    }

    text(
        title = R.string.preference_appearance_theme,
        getter = AppearancePrefs.Theme::index,
        setter = { AppearancePrefs.Theme.index = it },
        builder = {
            onClick = ::showThemeChooserDialog
            textGetter = { string(Themes(it).titleRes) }
        }
    )

    fun KPrefColorPicker.KPrefColorContract.dependsOnCustom() {
        enabler = AppearancePrefs.Theme::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
        allowCustom = true
    }

    colorPicker(
        title = R.string.preference_appearance_color_text,
        getter = AppearancePrefs.Theme.Custom::textColor,
        setter = { textColor ->
            AppearancePrefs.Theme.Custom.textColor = textColor
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
        getter = AppearancePrefs.Theme.Custom::accentColor,
        setter = { accentColor ->
            AppearancePrefs.Theme.Custom.accentColor = accentColor
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
        getter = AppearancePrefs.Theme.Custom::bgColor,
        setter = { backgroundColor ->
            AppearancePrefs.Theme.Custom.bgColor = backgroundColor
            bgCanvas.ripple(color = backgroundColor, duration = 500L)
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
        getter = AppearancePrefs.Theme.Custom::headerColor,
        setter = { headerColor ->
            AppearancePrefs.Theme.Custom.headerColor = headerColor
            themeNavigationBar()
            toolbarCanvas.ripple(
                color = headerColor,
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
        getter = AppearancePrefs.Theme.Custom::iconColor,
        setter = { iconColor ->
            AppearancePrefs.Theme.Custom.iconColor = iconColor
            invalidateOptionsMenu()
            shouldRestartMain()
        },
        builder = {
            dependsOnCustom()
            allowCustomAlpha = false
        }
    )

    header(R.string.preference_appearance_global_header)

    fun showMainActivityLayoutChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_appearance_main_activity_layout)
            items(MainActivityLayouts.values().map { mainActivityLayout ->
                string(mainActivityLayout.titleRes)
            })
            with(onClick) {
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

    text(
        title = R.string.preference_appearance_main_activity_layout,
        getter = AppearancePrefs.MainActivityLayout::index,
        setter = { AppearancePrefs.MainActivityLayout.index = it },
        builder = {
            textGetter = { string(AppearancePrefs.MainActivityLayout.titleRes) }
            onClick = ::showMainActivityLayoutChooserDialog
        }
    )

    fun showDateTimeFormatChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_appearance_date_time_format)
            items(DateTimeFormats.values().map { it.previewText })
            with(onClick) {
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
    }

    text(
        title = R.string.preference_appearance_date_time_format,
        getter = AppearancePrefs.DateTimeFormat::index,
        setter = { AppearancePrefs.DateTimeFormat.index = it },
        builder = {
            onClick = ::showDateTimeFormatChooserDialog
            textGetter = { string(DateTimeFormats(it).titleRes) }
        }
    )

    checkbox(
        title = R.string.preference_appearance_tint_navbar,
        getter = AppearancePrefs::tintNavBar,
        setter = { tintNavBar ->
            AppearancePrefs.tintNavBar = tintNavBar
            themeNavigationBar()
            setAardvarkResult(MainActivity.REQUEST_NAV)
        },
        builder = { descRes = R.string.preference_appearance_tint_navbar_desc }
    )

}