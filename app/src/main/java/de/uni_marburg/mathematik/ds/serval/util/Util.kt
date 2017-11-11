package de.uni_marburg.mathematik.ds.serval.util

import android.app.Activity
import de.uni_marburg.mathematik.ds.serval.Aardvark

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun consumeIf(predicate: Boolean, f: () -> Unit): Boolean {
    if (predicate) {
        f()
        return true
    }
    return false
}

fun Activity.setCurrentScreen() =
        Aardvark.firebaseAnalytics.setCurrentScreen(this, this::class.java.simpleName, null)