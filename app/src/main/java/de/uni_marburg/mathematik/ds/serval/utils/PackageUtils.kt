package de.uni_marburg.mathematik.ds.serval.utils

import android.os.Build

inline val buildIsOreoAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

const val GOOGLE_MAPS = "com.google.android.apps.maps"