package de.uni_marburg.mathematik.ds.serval.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun consume() {
        assertEquals(consume { print("any function") }, true)
    }
}