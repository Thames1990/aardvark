package de.uni_marburg.mathematik.ds.serval.util

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

object Preferences : KPref() {
    /** Determines whether the user is asked to confirm the intention of exiting the application */
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)

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
     * Determines whether WiFi ADB is activated.
     *
     * This is only available with the debug version.
     */
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)

    /** Stores the version number of the app **/
    var version: Int by kpref("VERSION", 1)
}
