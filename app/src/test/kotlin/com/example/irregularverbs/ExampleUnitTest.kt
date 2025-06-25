package com.example.irregularverbs

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun copyAllExceptIndex_isCorrect() {
        val source = listOf("str0", "str1", "str2", "str3", "str4")
        val target = source.copyAllExceptIndex(2)
        assertEquals(listOf("str0", "str1", "str3", "str4"), target)
    }
}