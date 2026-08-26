# Kanji Time

A simple open source Android home screen widget that shows a unique kanji, its
pronunciation and meaning.

## Platform & stack

- **Android only, native Kotlin.** No Flutter: home screen widgets are
  inherently platform-specific (AppWidgetProvider/RemoteViews), so Flutter
  would add a bridge to native code without unifying the widget layer.
- **Traditional `AppWidgetProvider` + `RemoteViews` XML.** No third-party
  libraries.
- **Min SDK 26 (Android 8.0), target latest stable.**
- Single module, standard Gradle project created from the Android Studio wizard.

## Core behaviors

- Minimalist lock screen and home screen widget. Home screen is the primary
  target; lock screen is best-effort (depends on launcher/OEM, native support
  was removed after Android 4.2).
- Follows system theme: **Material You dynamic colors on Android 12+ (API 31)**,
  plain white/light fallback below that; dark-mode aware.
- Follows Android widget conventions (resizable, content descriptions, sensible
  update on configuration changes).
- Changes every selected interval, default **every hour** (uses
  `updatePeriodMillis`; Android enforces a 30-minute minimum and updates may
  drift slightly under Doze — acceptable for v1).
- Rotation: **random kanji per widget instance** on each update.

## Layout (2 rows x 4 columns)

- **Kanji**: 2 rows high x 2 columns wide, aligned left.
- **Pronunciation**: 1 row high x 2 columns wide, aligned right.
- **Meaning**: 1 row high x 2 columns wide, aligned right, below pronunciation.

## Data model

Each entry:

```json
{
  "kanji": "水",
  "reading": "mizu",
  "meaning": "water"
}
```

- Bundled JSON asset with a **curated starter set of ~60-80 common kanji**.
- **English only for v1** (English meanings derived from KANJIDIC/JMdict, AI
  reviewed).
- **Reading shown**: the single most common reading.

## App shell

- Minimal settings activity:
  - Update interval selector (default 1h; options 1/2/6/12/24h).
  - Language selector (English only for v1; Spanish UI strings reserved).
- Language only affects the UI for v1; meaning-language switching is a v2 data
  concern.

## Out of scope for v1

- Spanish meanings (reserved for a future data source/translation).
- User-selectable reading style (onyomi/kunyomi) — consider later.
- Extra per-entry fields (JLPT level, stroke count, example word).
- Exact scheduling via AlarmManager/WorkManager.
- iOS/desktop/web.

## Future considerations

- Meaning-language switching (EN to ES) when bilingual data exists.
- Reading style selector (onyomi / kunyomi / most common).
- Exact update scheduling if `updatePeriodMillis` drift is ever a problem.
