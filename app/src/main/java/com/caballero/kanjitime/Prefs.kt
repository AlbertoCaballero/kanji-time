package com.caballero.kanjitime

import android.content.Context

object Prefs {

    private const val PREFS_NAME = "kanji_time"
    private const val KEY_INTERVAL_MS = "interval_ms"
    private const val KEY_LANGUAGE = "language"
    private const val HOUR_MS = 60L * 60 * 1000

    const val DEFAULT_INTERVAL_MS = HOUR_MS

    val INTERVAL_OPTIONS_MS: List<Long> = listOf(HOUR_MS, 2 * HOUR_MS, 6 * HOUR_MS, 12 * HOUR_MS, 24 * HOUR_MS)

    fun intervalMs(context: Context): Long =
        prefs(context).getLong(KEY_INTERVAL_MS, DEFAULT_INTERVAL_MS)

    fun setIntervalMs(context: Context, ms: Long) {
        prefs(context).edit().putLong(KEY_INTERVAL_MS, ms).apply()
    }

    fun language(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "en") ?: "en"

    fun setLanguage(context: Context, language: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
