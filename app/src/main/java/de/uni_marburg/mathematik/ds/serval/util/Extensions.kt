package de.uni_marburg.mathematik.ds.serval.util

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}