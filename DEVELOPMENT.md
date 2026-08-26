# Kanji Time — Development Plan

## 1. Environment setup (EndeavourOS / Arch)

Prerequisites: ~8 GB free disk, a physical device or emulator.

`android-studio` and the SDK command-line tools are **AUR** packages on Arch;
adb and the JDK come from the official repos.

### System update + JDK 21 + adb (official repos)

```sh
sudo pacman -Syu
sudo pacman -S jdk21-openjdk android-tools
```

If a previous JDK was the system default, point Arch at 21:

```sh
archlinux-java status        # check
sudo archlinux-java set java-21-openjdk
```

### Path A — Android Studio (recommended)

```sh
yay -S android-studio
```

Launch Android Studio once, then in **SDK Manager** install:

- Android SDK Platform (latest stable, e.g. 36) + a compatible Build-Tools
- Emulator + a system image (e.g. `android-36 google_apis x86_64`)

Default SDK path is `~/Android/Sdk` (user-writable). `adb` comes from
`android-tools`.

### Path B — command-line-only SDK (no Studio GUI)

```sh
yay -S android-sdk-cmdline-tools-latest   # provides sdkmanager in /opt/android-sdk
sudo chown -R "$USER": "$ANDROID_HOME"    # AUR install is root-owned
```

Add to `~/.zshrc`:

```sh
export ANDROID_HOME=/opt/android-sdk
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin"
```

Reload, then install packages (check exact versions with `sdkmanager --list`):

```sh
sdkmanager --install "platforms;android-36" "build-tools;36.0.0" "emulator" "system-images;android-36;google_apis;x86_64"
sdkmanager --licenses
```

### Create an emulator (AVD)

```sh
avdmanager create avd -n kanji_pixel -k "system-images;android-36;google_apis;x86_64" -d pixel_6
emulator -list-avds
```

### Verify

```sh
java -version
adb version
```

### Init git (optional)

```sh
git init && git add -A && git commit -m "Initial commit"
```

## 2. Project phases

### Phase 1 — Scaffold

- **Toolchain**: Gradle wrapper 9.7.1 (supports JDK 26), AGP 9.3.0 (default
  Build Tools 36.0.0), built-in Kotlin (no `org.jetbrains.kotlin.android`
  plugin needed — AGP 9.0+ bundles it). Compile/target SDK 36, min SDK 26.
  Package `com.caballero.kanjitime`.
- `res/xml/kanji_widget_info_{1h,2h,6h,12h,24h}.xml` (appwidget-provider),
  one per interval, identical except `updatePeriodMillis`.
- The manifest declares the 1h config under the standard
  `android.appwidget.provider` meta-data key plus the four alternatives under
  custom keys. Changing the interval calls
  `AppWidgetManager.updateAppWidgetProviderInfo(provider, metaDataKey)`
  (API 28+), then broadcasts an update.
- Register `KanjiWidgetProvider` and the settings `MainActivity` in the manifest.

### Phase 2 — Data layer

- Add `assets/kanji.json`: ~60-80 curated common kanji
  (`{ kanji, reading, meaning }`, English).
- `KanjiRepository`: lazy-load JSON, parse into data class, expose `random()`
  (avoid repeating the last shown kanji per widget instance via `SharedPreferences`).
- `CustomKanjiStore`: user-added entries persisted to `custom_kanji.json` in app
  files; manual add/remove and JSON import (SAF); merged with the bundled set
  in `KanjiRepository.merged` (custom overrides bundled on kanji match).

### Phase 3 — Widget

- `res/layout/widget_kanji.xml` implementing the 2x4 grid:
  kanji (2x2, left), reading (top right), meaning (below right).
- `KanjiWidgetProvider.kt`: on `onUpdate`, pick a random kanji, populate
  `RemoteViews`, tint for Material You on API 31+ (`android.R.color.system_*`
  palette), fall back to white/light theme below; dark variant via
  `values-night/colors.xml`.
- `onAppWidgetOptionsChanged` shows a new kanji on resize.
- Wire a click-through to the app and add content descriptions.

### Phase 4 — Settings app

- `MainActivity`: interval selector (1/2/6/12/24h, default 1h) and language
  selector (English now; Spanish UI strings reserved in `strings.xml`).
- Persist via `SharedPreferences`; widget reads prefs on each update.
- Prompt/re-bind widgets when interval changes.

### Phase 5 — Verification

- `./gradlew lint` and `./gradlew test`.
- Add a unit test for rotation logic (no immediate repeats, JSON validity).
- Run on emulator: place widget, force update, check layout at multiple
  widget sizes, check dark mode and Material You colors.

### Phase 6 — Docs & release prep

- README with feature list, screenshots, and "add to home screen" instructions.
- Note lock screen caveat (OEM-dependent).
- Future roadmap from SPECS.md.

## 3. Build & test commands

```sh
./gradlew assembleDebug        # build APK
./gradlew lint                 # static analysis
./gradlew test                 # unit tests
./gradlew installDebug         # install on connected device/emulator
```

## 4. Definition of done

- Widget renders correct 2x4 layout at default and resized sizes.
- Kanji changes per interval; random per widget instance.
- Material You colors on API 31+; white/light fallback below; dark mode works.
- Settings persist and take effect on next widget update.
- `lint` and unit tests pass.
