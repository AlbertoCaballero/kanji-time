package com.caballero.kanjitime

import org.junit.Assert.assertEquals
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
    fun `merged keeps bundled order and appends new custom entries`() {
        val bundled = KanjiParser.parse(sampleJson)
        val custom = listOf(KanjiEntry("犬", "inu", "dog"), KanjiEntry("猫", "neko", "cat"))
        val merged = KanjiRepository.merged(bundled, custom)
        assertEquals(listOf("水", "山", "火", "犬", "猫"), merged.map { it.kanji })
    }

    @Test
    fun `merged lets custom entries override bundled ones with the same kanji`() {
        val bundled = KanjiParser.parse(sampleJson)
        val custom = listOf(KanjiEntry("水", "mizu", "water, fluid"))
        val merged = KanjiRepository.merged(bundled, custom)
        assertEquals(3, merged.size)
        assertEquals(KanjiEntry("水", "mizu", "water, fluid"), merged[0])
    }
}
