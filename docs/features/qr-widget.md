# Feature: qr-widget

> Sprint 3 schema lock. Glance shows a cached square QR; tap boosts brightness for scanning.

## Acceptance criteria

- ✅ Quick Settings tile opens BrightenActivity for the selected gallery card
- ✅ TalkBack is `QR code for {name}` and never includes a Wi-Fi password
- ✅ Config: pick profile, caption, carousel Prev/Next (opt-in), sensitive lock, transparent — **per widget instance** (no shared bleed)
- ✅ Carousel wraps all gallery codes when enabled for that widget only
- ✅ Sensitive cards require device PIN / biometric before Brighten loads payload
- ✅ Offline/error: empty cache is non-fatal; widget still renders a label
- ✅ Accessibility: widget description stays in `widget_description`; no new untranslated UI in logic
- ✅ i18n: `widget_*` strings only in `examples/android/widget/src/main/res/values/strings.xml`

## Smoke scenario

1. _Given_ a cache entry for profile `home` at 256px
2. _When_ `QrWidgetCache.get` is called with the same key
3. _Then_ width/height match and pixels equal the stored raster

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/widget/src/main/kotlin/org/qraft/widget/QrWidgetCache.kt`, `BrightenAction.kt`, `BrightenTile.kt` |
| View | `QrGlanceWidget.kt`, `BrightenTileService.kt` |
| Tests | `examples/android/widget/src/test/kotlin/org/qraft/widget/` |
| Wiring | receiver + QS tile in widget manifest; app composition root unchanged this sprint |

## Tests

- Automated: yes — `QrWidgetCacheTest`, `BrightenActionTest`, `BrightenTileTest`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
object BrightenAction {
    const val ACTION = "org.qraft.widget.action.BRIGHTEN"
    const val WINDOW_BRIGHTNESS = 1.0f
}

class QrWidgetCache {
    fun put(key: String, width: Int, height: Int, pixels: IntArray)
    fun get(key: String): CachedBitmap?
    fun key(profileId: String, styleJson: String, sizePx: Int): String
}

```
