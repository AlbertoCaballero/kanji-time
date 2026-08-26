package com.caballero.kanjitime

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

class CustomKanjiStore(context: Context) {

    private val file = File(context.filesDir, CUSTOM_FILE)

    private val entries: MutableList<KanjiEntry> by lazy {
        if (file.exists()) {
            KanjiParser.parse(file.readText()).toMutableList()
        } else {
            mutableListOf()
        }
    }

    fun all(): List<KanjiEntry> = entries.toList()

    fun add(entry: KanjiEntry): Boolean {
        if (entry.kanji.isBlank() || entry.reading.isBlank() || entry.meaning.isBlank()) {
            return false
        }
        entries.removeAll { it.kanji == entry.kanji }
        entries.add(entry)
        persist()
        return true
    }

    fun remove(kanji: String): Boolean {
        val removed = entries.removeAll { it.kanji == kanji }
        if (removed) persist()
        return removed
    }

    fun importJson(text: String): Int {
        val parsed = KanjiParser.parse(text)
        for (entry in parsed) {
            if (entry.kanji.isNotBlank() && entry.reading.isNotBlank() && entry.meaning.isNotBlank()) {
                entries.removeAll { it.kanji == entry.kanji }
                entries.add(entry)
            }
        }
        persist()
        return parsed.size
    }

    private fun persist() {
        val json = JSONArray()
        for (entry in entries) {
            json.put(JSONObject().apply {
                put("kanji", entry.kanji)
                put("reading", entry.reading)
                put("meaning", entry.meaning)
            })
        }
        file.writeText(json.toString())
    }

    companion object {
        private const val CUSTOM_FILE = "custom_kanji.json"
    }
}
