package de.uni_marburg.mathematik.ds.serval.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsKtTest {

    @Test
    fun consume() = assertEquals(consume { }, true)

    @Test
    fun consumeIf() {
        assertEquals(consumeIf(true) {}, true)
        assertEquals(consumeIf(false) {}, false)
    }
}