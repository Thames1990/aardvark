package de.uni_marburg.mathematik.ds.serval.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilKtTest {

    @Test
    fun consume() = assertEquals(consume { }, true)

    @Test
    fun consumeIf() {
        assertEquals(consumeIf(true) {}, true)
        assertEquals(consumeIf(false) {}, false)
    }
}