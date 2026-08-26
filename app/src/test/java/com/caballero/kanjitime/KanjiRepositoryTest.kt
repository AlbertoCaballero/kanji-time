package com.caballero.kanjitime

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KanjiRepositoryTest {

    private val sampleJson = """
        [
          { "kanji": "水", "reading": "mizu", "meaning": "water" },
          { "kanji": "山", "reading": "yama", "meaning": "mountain" },
          { "kanji": "火", "reading": "hi", "meaning": "fire" }
        ]
    """.trimIndent()

    @Test
    fun `parse reads all entries`() {
        val entries = KanjiParser.parse(sampleJson)
        assertEquals(3, entries.size)
        assertEquals(KanjiEntry("水", "mizu", "water"), entries[0])
        assertEquals("fire", entries[2].meaning)
    }

    @Test
    fun `pickIndex never repeats the last shown index`() {
        val random = Random(42)
        repeat(200) {
            val size = 5
            val last = random.nextInt(size)
            val picked = KanjiRepository.pickIndex(size, last, random)
            assertNotEquals(last, picked)
            assertTrue(picked in 0 until size)
        }
    }

    @Test
    fun `pickIndex handles a single entry`() {
        assertEquals(0, KanjiRepository.pickIndex(1, 0, Random(0)))
    }
}
