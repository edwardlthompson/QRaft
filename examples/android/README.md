<p align="center">
  <img src="../../branding/assets/logo-mark.png" alt="QRaft" width="96" />
</p>

# QRaft Android (FOSS)

Offline QR crafting for LineageOS / AOSP — Compose UI, Glance widgets, wallpaper safe-zone.

## Modules

```text
examples/android/
  :app         Compose UI (org.qraft.app)
  :core-qr     Nayuki encode + QrMatrix + SquareRasterizer (JVM)
  :render      Bitmap wrappers / future style renderers
  :widget      Jetpack Glance AppWidget (home + keyguard)
  :wallpaper   Safe-zone math + wallpaper export path
  :data        Profile repository scaffold

```

**minSdk 26** (Android 8.0) — F-Droid-friendly floor. Primary target: LineageOS on Android 11+.

**Styles and strings are separate:** theme tokens in `ui/theme/`; copy in `res/values/strings.xml` via `stringResource`.

## Build

```bash
export JAVA_HOME=…          # JDK 17+
export ANDROID_HOME=…
export SOURCE_DATE_EPOCH=1700000000
cd examples/android
./gradlew :core-qr:test :wallpaper:test :app:assembleDebug

```

## FOSS compliance

No Google Play Services, Firebase, or closed telemetry. No `INTERNET` permission. Vendored Nayuki QR-Code-generator (MIT) under `core-qr/`.
