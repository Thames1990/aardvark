package de.uni_marburg.mathematik.ds.serval.utils

import android.os.Build
import de.uni_marburg.mathematik.ds.serval.BuildConfig

inline val buildIsOreoAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

inline val isDebugBuild: Boolean
    get() = BuildConfig.DEBUG

inline val isReleaseBuild: Boolean
    get() = !isDebugBuild

inline val analyticsEnabled: Boolean
    get() = isReleaseBuild && Prefs.Behaviour.analyticsEnabled

const val GOOGLE_MAPS = "com.google.android.apps.maps"