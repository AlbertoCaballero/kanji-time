# Kanji Time

A simple open source Android home screen widget that shows a unique kanji, its
pronunciation and meaning.

## Features

- Minimalist 2x4 home screen widget: large kanji on the left, reading and
  meaning aligned right.
- Follows the system theme: Material You dynamic colors on Android 12+, plain
  white/light (or dark) theme otherwise.
- Changes to a new kanji every hour by default. Adjustable to 2, 6, 12 or
  24 hours from the settings screen.
- Random kanji per widget instance, never repeating the same one twice in a row.
- Curated starter set of ~80 common kanji (English meanings), bundled offline.
- No network access, no accounts, no third-party libraries.

## Requirements

- Android 8.0 (API 26) or newer.

## Getting started

```sh
git clone git@github.com:<you>/kanji-time.git
cd kanji-time
./gradlew assembleDebug
```

Set your Android SDK location either via `ANDROID_HOME` or an uncommitted
`local.properties` file:

```properties
sdk.dir=/path/to/android-sdk
```

### Add the widget

1. Install the app and open it once to configure settings (optional).
2. Long-press the home screen and pick **Kanji Time** from the widgets.

The widget will pick a new kanji on every update. Tapping it opens the app.

## Project layout

- `app/src/main/java/com/caballero/kanjitime/KanjiWidgetProvider.kt` — the widget.
- `app/src/main/java/com/caballero/kanjitime/KanjiRepository.kt` — data + rotation.
- `app/src/main/java/com/caballero/kanjitime/MainActivity.kt` — settings screen.
- `app/src/main/assets/kanji.json` — the bundled kanji dataset.
- `app/src/main/res/xml/kanji_widget_info_*h.xml` — per-interval widget configs.

## Roadmap

- Spanish meanings (meaning-language switching).
- User-selectable reading style (onyomi / kunyomi).
- Example words per kanji.
- Larger dataset (JLPT / Jouyou).

## License

Open source (add your preferred license here).
