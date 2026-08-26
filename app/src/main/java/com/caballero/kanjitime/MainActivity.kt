package com.caballero.kanjitime

import android.app.Activity
import android.os.Bundle
import android.widget.RadioGroup

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val intervalGroup = findViewById<RadioGroup>(R.id.interval_group)
        intervalGroup.check(intervalToButtonId(Prefs.intervalMs(this)))
        intervalGroup.setOnCheckedChangeListener { _, checkedId ->
            val ms = buttonIdToInterval(checkedId)
            Prefs.setIntervalMs(this, ms)
            KanjiWidgetProvider.updateProviderInfo(this, ms)
        }

        val languageGroup = findViewById<RadioGroup>(R.id.language_group)
        languageGroup.check(if (Prefs.language(this) == "es") R.id.language_spanish else R.id.language_english)
        languageGroup.setOnCheckedChangeListener { _, checkedId ->
            Prefs.setLanguage(this, if (checkedId == R.id.language_spanish) "es" else "en")
        }
    }

    private fun intervalToButtonId(ms: Long): Int = when (ms) {
        2L * HOUR_MS -> R.id.interval_2h
        6L * HOUR_MS -> R.id.interval_6h
        12L * HOUR_MS -> R.id.interval_12h
        24L * HOUR_MS -> R.id.interval_24h
        else -> R.id.interval_1h
    }

    private fun buttonIdToInterval(id: Int): Long = when (id) {
        R.id.interval_2h -> 2L * HOUR_MS
        R.id.interval_6h -> 6L * HOUR_MS
        R.id.interval_12h -> 12L * HOUR_MS
        R.id.interval_24h -> 24L * HOUR_MS
        else -> HOUR_MS
    }

    private companion object {
        const val HOUR_MS = 60L * 60 * 1000
    }
}
