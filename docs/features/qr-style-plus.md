# Feature: qr-style-plus

> Sprint 10 session 1. Auto ECC, color themes, gradients, image backgrounds, center icons, caption band.

## Acceptance criteria

- ✅ User-visible: Home has no ECC dropdown; ECC text matches overlay/wallpaper/widget policy
- ✅ Themes, optional gradient (eyes solid), optional caption under the QR (image taller, not wider)
- ✅ Center icons / cut-out force ECC H; wallpaper blit stays square (no caption)
- ✅ Invalid `styleJson` loads `QrStyle.DEFAULT`

## Smoke scenario

1. Given a URL on Home
2. When the user picks Sunset theme, a center link icon, and caption “Scan me”
3. Then the preview updates live; export PNG is taller than wide; wallpaper path stays square

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/core-qr/.../EccPolicy.kt`, `examples/android/render/.../QrStyle.kt` |
| View | `examples/android/app/.../ui/stylepanel/`, `ui/editor/` |
| Tests | `EccPolicyTest`, `QrStyleTest`, caption/gradient tests |
| Wiring | `EditorPreview` encode + `StyleControls` |

## Tests

- Automated: yes — `EccPolicyTest`, rasterizer caption/gradient/inlay tests

## Fallback validation

- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
enum class QrSurface { EDITOR, WIDGET, WALLPAPER, EXPORT }
object EccPolicy {
    fun choose(surface: QrSurface, hasOverlay: Boolean): ErrorCorrectionLevel
}
enum class ColorTheme { CLASSIC, OLED, INVERT, BLUE, FOREST, SUNSET }
enum class CenterMark { NONE, CUTOUT, LINK, WIFI, PERSON, EMAIL, PHONE, SMS, CRYPTO, GITHUB, MASTODON, MATRIX, GLOBE }
data class GradientSpec(val enabled: Boolean = false, val startArgb: Int, val endArgb: Int)
data class CaptionSpec(val text: String = "")
```

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty caption | Skip compose; test |
| Network timeout | N/A |
| Race | Single Compose style JSON |
| Unhandled JSON | decode → DEFAULT |
