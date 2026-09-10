# Feature: qr-style

> Sprint 2 schema lock. Module/finder styling is paint on a locked `QrMatrix` — never re-encode.

## Acceptance criteria

- ✅ User-visible: square / rounded / dot / diamond / pill / blob modules; square / rounded / circle / hex / ring finders with independent pupil; OLED bg; optional center logo cut-out
- ✅ Empty or invalid `styleJson` loads `QrStyle.DEFAULT` (black-on-white squares, no cut-out)
- ✅ Logo cut-out diameter is clamped to `0.10..0.28` of the symbol; enabled cut-out requires encode with `forceHighEcc`
- ✅ Accessibility: scannability warnings via `Scannability.analyze` (contrast, quiet zone, logo coverage, ECC)
- ✅ i18n: no user-facing strings in `:render` / `:data`; labels stay in `app` `strings.xml` later

## Smoke scenario

1. _Given_ a URL payload encoded to `QrMatrix` with ECC H
2. _When_ `StyledQrRasterizer.rasterize` runs with rounded modules, rounded finders, and a 0.20 cut-out
3. _Then_ output is square, deterministic, quiet zone ≥ 4 modules, and the center cut-out is background ARGB

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/render/src/main/kotlin/org/qraft/render/QrStyle.kt`, `QrStyleJson.kt`, `StyledQrRasterizer.kt` |
| View | `StyleControls` on Home (`EditorScreen`); `StyledQrRenderer` Bitmap wrap |
| Tests | `examples/android/render/src/test/kotlin/org/qraft/render/` |
| Persistence | `examples/android/data/.../ProfileCodec.kt`, `DataStoreProfileRepository.kt` (`styleJson` opaque) |
| Wiring | none this sprint (home preview stays square until a later wire row) |

## Tests

- Automated: yes — `QrStyleJsonTest`, `StyledQrRasterizerTest`, `ProfileCodecTest`, `DataStoreProfileRepositoryTest`

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
enum class ModuleShape { SQUARE, ROUNDED, DOT, DIAMOND, PILL_H, PILL_V, BLOB }
enum class FinderShape { SQUARE, ROUNDED, CIRCLE, HEX, RING }

data class QrStyle(
    val moduleShape: ModuleShape = ModuleShape.SQUARE,
    val finderShape: FinderShape = FinderShape.SQUARE,
    val finderPupil: FinderShape = FinderShape.SQUARE,
    val foregroundArgb: Int = 0xFF000000.toInt(),
    val eyeColorArgb: Int = 0xFF000000.toInt(),
    val backgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val quietZoneModules: Int = Scannability.MIN_QUIET_ZONE_MODULES,
    val logoCutout: LogoCutout = LogoCutout(),
) { val hasOverlay: Boolean }

object QrStyleJson {
    fun encode(style: QrStyle): String
    fun decode(json: String): QrStyle // blank / invalid → DEFAULT
}

object StyledQrRasterizer {
    fun rasterize(matrix: QrMatrix, sizePx: Int, style: QrStyle = QrStyle.DEFAULT)
        : SquareRasterizer.Result
}

object StyledQrRenderer {
    fun render(matrix: QrMatrix, sizePx: Int, style: QrStyle = QrStyle.DEFAULT): Bitmap
}
```

`QrStyle.DEFAULT` is all-default constructor. Encode path does not live in `:render`; callers pass `QrEncoder.encode(..., forceHighEcc = style.logoCutout.enabled)`.

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty `styleJson` | `QrStyleJson.decode` returns `DEFAULT`; `ProfileCodec.decodeList` skips blank ids |
| Network timeout | N/A — no network I/O |
| Race on DataStore | Single `DataStore.edit` per mutation; tests use `runTest` |
| Unhandled JSON exceptions | `decode` / `decodeList` catch and fall back to defaults / empty list |
