package de.uni_marburg.mathematik.ds.serval.utils

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

val Fragment.currentActivity: FragmentActivity
    get() = activity ?: throw IllegalStateException("Activity must not be null")

val Fragment.currentContext: Context
    get() = context ?: throw IllegalStateException("Context must not be null")