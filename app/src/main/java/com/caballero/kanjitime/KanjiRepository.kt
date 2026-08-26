package com.caballero.kanjitime

import android.content.Context
import android.content.SharedPreferences
import kotlin.random.Random
import org.json.JSONArray

data class KanjiEntry(
    val kanji: String,
    val reading: String,
    val meaning: String,
)

object KanjiParser {
    fun parse(json: String): List<KanjiEntry> {
        val array = JSONArray(json)
        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            KanjiEntry(
                kanji = obj.getString("kanji"),
                reading = obj.getString("reading"),
                meaning = obj.getString("meaning"),
            )
        }
    }
}

class KanjiRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val entries: List<KanjiEntry> by lazy {
        val json = context.assets.open(KANJI_ASSET).bufferedReader().use { it.readText() }
        KanjiParser.parse(json)
    }

    fun randomEntry(widgetId: Int, random: Random = Random.Default): KanjiEntry {
        val last = prefs.getInt(KEY_LAST_INDEX + widgetId, NO_INDEX)
        val index = pickIndex(entries.size, last, random)
        prefs.edit().putInt(KEY_LAST_INDEX + widgetId, index).apply()
        return entries[index]
    }

    companion object {
        private const val PREFS_NAME = "kanji_time"
        private const val KEY_LAST_INDEX = "last_index_"
        private const val NO_INDEX = -1
        private const val KANJI_ASSET = "kanji.json"

        internal fun pickIndex(size: Int, last: Int, random: Random): Int {
            if (size <= 1) return 0
            var index = random.nextInt(size)
            if (index == last) index = (index + 1) % size
            return index
        }
    }
}
