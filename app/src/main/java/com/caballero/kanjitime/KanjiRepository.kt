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

    private val bundled: List<KanjiEntry> by lazy {
        val json = context.assets.open(KANJI_ASSET).bufferedReader().use { it.readText() }
        KanjiParser.parse(json)
    }

    private val custom = CustomKanjiStore(context)

    fun allEntries(): List<KanjiEntry> = merged(bundled, custom.all())

    fun randomEntry(widgetId: Int, random: Random = Random.Default): KanjiEntry {
        val all = allEntries()
        if (all.isEmpty()) return KanjiEntry("?", "?", "?")
        val last = prefs.getString(KEY_LAST_KANJI + widgetId, null)
        var index = random.nextInt(all.size)
        if (all.size > 1 && all[index].kanji == last) index = (index + 1) % all.size
        prefs.edit().putString(KEY_LAST_KANJI + widgetId, all[index].kanji).apply()
        return all[index]
    }

    companion object {
        private const val PREFS_NAME = "kanji_time"
        private const val KEY_LAST_KANJI = "last_kanji_"
        private const val KANJI_ASSET = "kanji.json"

        internal fun merged(bundled: List<KanjiEntry>, custom: List<KanjiEntry>): List<KanjiEntry> {
            val byKanji = LinkedHashMap<String, KanjiEntry>()
            for (entry in bundled) byKanji[entry.kanji] = entry
            for (entry in custom) byKanji[entry.kanji] = entry
            return byKanji.values.toList()
        }
    }
}
