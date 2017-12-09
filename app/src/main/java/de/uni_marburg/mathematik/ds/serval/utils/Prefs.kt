package de.uni_marburg.mathematik.ds.serval.utils

import android.graphics.Color
import android.view.WindowManager
import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.isColorVisibleOn
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.enums.AARDVARK_GREEN
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.Theme

object Prefs : KPref() {

    var lastLaunch: Long by kpref("LAST_LAUNCH", -1L)

    var kervalBaseUrl: String by kpref("KERVAL_BASE_URL", "serval.splork.de")
    var kervalPassword: String by kpref("KERVAL_PASSWORD", "pum123")
    var kervalPort: Int by kpref("KERVAL_PORT", 80)
    var kervalUser: String by kpref("KERVAL_USER", "pum")

    var theme: Int by kpref("theme", 0, postSetter = { _: Int -> loader.invalidate() })

    var customTextColor: Int by kpref("color_text", 0xffeceff1.toInt())

    var customAccentColor: Int by kpref("color_accent", 0xff0288d1.toInt())

    var customBackgroundColor: Int by kpref("color_bg", 0xff212121.toInt())

    var customHeaderColor: Int by kpref("color_header", 0xff01579b.toInt())

    var customIconColor: Int by kpref("color_icons", 0xffeceff1.toInt())

    var exitConfirmation: Boolean by kpref("CONFIRM_EXIT", true)

    var versionCode: Int by kpref("VERSION", -1)

    var installDate: Long by kpref("install_date", -1L)

    var identifier: Int by kpref("identifier", -1)

    private val loader = lazyResettable { Theme.values[theme] }

    private val t: Theme by loader

    val textColor: Int
        get() = t.textColor

    val accentColor: Int
        get() = t.accentColor

    val accentColorForWhite: Int
        get() = when {
            accentColor.isColorVisibleOn(Color.WHITE) -> accentColor
            textColor.isColorVisibleOn(Color.WHITE)   -> textColor
            else                                      -> AARDVARK_GREEN
        }

    val bgColor: Int
        get() = t.bgColor

    val headerColor: Int
        get() = t.headerColor

    val iconColor: Int
        get() = t.iconColor

    val aardvarkId: String
        get() = "$installDate-$identifier"

    val isCustomTheme: Boolean
        get() = t == Theme.CUSTOM

    var tintNavBar: Boolean by kpref("TINT_NAV_BAR", true)

    val animate: Boolean by kpref("ANIMATE", true)

    var analytics: Boolean by kpref("USE_ANALYTICS", true)

    var debugSettings: Boolean by kpref("DEBUG_SETTINGS", BuildConfig.DEBUG)

    var mainActivityLayoutType: Int by kpref("main_activity_layout_type", 0)

    val mainActivityLayout: MainActivityLayout
        get() = MainActivityLayout(mainActivityLayoutType)

    var colorPrimary: Int by kpref("COLOR_PRIMARY", 0xff4CAF50.toInt())

    /**
     * Determines whether the application has been launched before.
     *
     * This value is set to false, if the user successfully logged in.
     */
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)

    /** Determines if the user is logged in to the Serval API. */
    var isLoggedIn: Boolean by kpref("IS_LOGGED_IN", false)

    /** Determines if a changelog is shown when a new versionCode is installed **/
    var changelog: Boolean by kpref("SHOW_CHANGELOG", true)

    /**
     * Determines whether [secure flag][WindowManager.LayoutParams.FLAG_SECURE] is set, which
     * disables screenshots and intercepting screen status of the app.
     */
    var useSecureFlag: Boolean by kpref("USE_SECURE_FLAG", true)

    /**
     * Determines whether WiFi ADB is activated.
     *
     * This is only available with the debug versionCode.
     */
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)
}
