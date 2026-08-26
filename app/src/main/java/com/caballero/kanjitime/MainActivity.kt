package com.caballero.kanjitime

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var customStore: CustomKanjiStore
    private lateinit var customList: LinearLayout
    private lateinit var editKanji: EditText
    private lateinit var editReading: EditText
    private lateinit var editMeaning: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        customStore = CustomKanjiStore(this)
        customList = findViewById(R.id.custom_list)
        editKanji = findViewById(R.id.edit_kanji)
        editReading = findViewById(R.id.edit_reading)
        editMeaning = findViewById(R.id.edit_meaning)

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

        findViewById<android.widget.Button>(R.id.custom_import).setOnClickListener { pickJsonFile() }
        findViewById<android.widget.Button>(R.id.custom_add).setOnClickListener { addManual() }

        refreshCustomList()
    }

    private fun pickJsonFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain"))
        }
        startActivityForResult(intent, REQ_IMPORT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_IMPORT && resultCode == RESULT_OK && data?.data != null) {
            try {
                contentResolver.openInputStream(data.data!!)?.use { input ->
                    val count = customStore.importJson(input.bufferedReader().use { it.readText() })
                    refreshCustomList()
                    KanjiWidgetProvider.requestWidgetRefresh(this)
                    toast(resources.getQuantityString(R.plurals.custom_imported_count, count, count))
                }
            } catch (e: Exception) {
                toast(getString(R.string.custom_import_error))
            }
        }
    }

    private fun addManual() {
        val entry = KanjiEntry(
            kanji = editKanji.text.toString().trim(),
            reading = editReading.text.toString().trim(),
            meaning = editMeaning.text.toString().trim(),
        )
        if (entry.kanji.isEmpty() || entry.reading.isEmpty() || entry.meaning.isEmpty()) {
            toast(getString(R.string.custom_required))
            return
        }
        val updated = customStore.all().any { it.kanji == entry.kanji }
        if (customStore.add(entry)) {
            editKanji.text.clear()
            editReading.text.clear()
            editMeaning.text.clear()
            refreshCustomList()
            KanjiWidgetProvider.requestWidgetRefresh(this)
            toast(getString(if (updated) R.string.custom_updated else R.string.custom_added, entry.kanji))
        }
    }

    private fun refreshCustomList() {
        customList.removeAllViews()
        val entries = customStore.all()
        if (entries.isEmpty()) {
            customList.addView(
                TextView(this).apply {
                    text = getString(R.string.custom_empty)
                    setTextColor(0xFF888888.toInt())
                    textSize = 14f
                    setPadding(0, dp(8), 0, dp(8))
                },
            )
            return
        }
        for (entry in entries) {
            customList.addView(
                TextView(this).apply {
                    text = getString(R.string.custom_row, entry.kanji, entry.reading, entry.meaning)
                    textSize = 16f
                    setPadding(0, dp(8), 0, dp(8))
                    isLongClickable = true
                    setOnLongClickListener {
                        confirmRemove(entry)
                        true
                    }
                },
            )
        }
    }

    private fun confirmRemove(entry: KanjiEntry) {
        AlertDialog.Builder(this)
            .setTitle(R.string.custom_remove_title)
            .setMessage(getString(R.string.custom_remove_message, entry.kanji))
            .setPositiveButton(R.string.custom_remove) { _, _ ->
                customStore.remove(entry.kanji)
                refreshCustomList()
                KanjiWidgetProvider.requestWidgetRefresh(this)
                toast(getString(R.string.custom_removed, entry.kanji))
            }
            .setNegativeButton(R.string.custom_cancel, null)
            .show()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        const val REQ_IMPORT = 1001
    }
}
