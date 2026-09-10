# Feature: qr-wallpaper

> Sprint 3 schema lock. Fit a square QR into `WallpaperSafeZone` and export/set pixels.

## Acceptance criteria

- ✅ Composer fills the full display with background ARGB and blits a square QR into `qrContentRect`
- ✅ User margin clamped 0–20%; leftover is background (OLED-aware), never stretched
- ✅ `WallpaperBinder` sets home / lock / both via `WallpaperManager` flags; SAF PNG from the wallpaper screen
- ✅ Empty matrix is rejected (`sizePx` / dimensions must be > 0)
- ✅ `WallpaperApplier` calls a setter with composed pixels (no network)
- ✅ Accessibility: wallpaper is a static image; scannability uses existing `Scannability` helpers
- ✅ i18n: no user-facing strings in `:wallpaper`

## Smoke scenario

1. _Given_ a 1080×2400 display and default 10% margin
2. _When_ `WallpaperComposer.compose` runs
3. _Then_ output size is 1080×2400, QR region is square inside the safe zone, and corner pixels are background

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/wallpaper/src/main/kotlin/org/qraft/wallpaper/WallpaperComposer.kt`, `WallpaperApplier.kt` |
| Tests | `examples/android/wallpaper/src/test/kotlin/org/qraft/wallpaper/` |
| Wiring | `SET_WALLPAPER` on wallpaper manifest; Activity hook later |

## Tests

- Automated: yes — `WallpaperComposerTest`, `WallpaperApplierTest`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
data class WallpaperImage(val width: Int, val height: Int, val pixels: IntArray)

object WallpaperComposer {
    fun compose(
        matrix: QrMatrix,
        totalWidthPx: Int,
        totalHeightPx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        marginFraction: Double = WallpaperSafeZone.DEFAULT_MARGIN_FRACTION,
    ): WallpaperImage
}

fun interface WallpaperSetter {
    fun setPixels(image: WallpaperImage)
}

class WallpaperApplier(private val setter: WallpaperSetter) {
    fun apply(image: WallpaperImage)
}
```
