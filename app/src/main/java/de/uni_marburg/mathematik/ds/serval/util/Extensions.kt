package de.uni_marburg.mathematik.ds.serval.util

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