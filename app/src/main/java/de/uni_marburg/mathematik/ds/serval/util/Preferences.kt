package de.uni_marburg.mathematik.ds.serval.util

import android.graphics.Color
import android.view.WindowManager
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

object Preferences : KPref() {
    /** Defines accent color */
    var colorAccent: Int by kpref("COLOR_ACCENT", 0xff82F7FF.toInt())

    /** Defines background color */
    var colorBackground: Int by kpref("COLOR_BACKGROUND", 0xff303030.toInt())

    var colorPrimary: Int by kpref("COLOR_PRIMARY", 0xff4CAF50.toInt())

    /** Defines text color */
    var colorText: Int by kpref("COLOR_TEXT", Color.WHITE)

    /** Determines whether the user is asked to confirm the intention of exiting the application */
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)

    var debugSettings: Boolean by kpref("DEBUG_SETTINGS", false)

    /**
     * Determines whether the application has been launched before.
     *
     * This value is set to false, if the user successfully logged in.
     */
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)

    /** Determines if the user is logged in to the Serval API. */
    var isLoggedIn: Boolean by kpref("IS_LOGGED_IN", false)

    /** Serval API URL */
    var kervalBaseUrl: String by kpref("KERVAL_BASE_URL", "serval.splork.de")

    /** Serval API password **/
    var kervalPassword: String by kpref("KERVAL_PASSWORD", "pum123")

    /** Serval API port **/
    var kervalPort: Int by kpref("KERVAL_PORT", 80)

    /** Serval API user **/
    var kervalUser: String by kpref("KERVAL_USER", "pum")

    /** Determines if a changelog is shown when a new version is installed **/
    var showChangelog: Boolean by kpref("SHOW_CHANGELOG", true)

    /** Determines whether anlytics collection is enabled */
    var useAnalytics: Boolean by kpref("USE_ANALYTICS", true)

    /**
     * Determines whether [secure flag][WindowManager.LayoutParams.FLAG_SECURE] is set, which
     * disables screenshots and intercepting screen status of the app.
     */
    var useSecureFlag: Boolean by kpref("USE_SECURE_FLAG", true)

    /**
     * Determines whether WiFi ADB is activated.
     *
     * This is only available with the debug version.
     */
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)

    /** Stores the version number of the app **/
    var version: Int by kpref("VERSION", 1)
}
