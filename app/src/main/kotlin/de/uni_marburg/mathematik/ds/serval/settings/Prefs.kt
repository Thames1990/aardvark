package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

object Prefs : KPref() {

    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)

}
