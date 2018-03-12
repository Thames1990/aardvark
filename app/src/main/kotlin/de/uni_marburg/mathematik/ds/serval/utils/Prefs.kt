package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.BuildConfig

// TODO Separate into different shared preferences
object Prefs : KPref() {

    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)

    object Serval {
        const val EVENT_COUNT = 10000

        var baseUrl: String by kpref(
            key = "SERVAL_BASE_URL",
            fallback = BuildConfig.SERVAL_BASE_URL
        )
        var password: String by kpref(
            key = "SERVAL_PASSWORD",
            fallback = BuildConfig.SERVAL_PASSWORD
        )
        var port: Int by kpref(key = "SERVAL_PORT", fallback = BuildConfig.SERVAL_PORT)
        var user: String by kpref(key = "SERVAL_USER", fallback = BuildConfig.SERVAL_USER)

        var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    }

}
