# Feature: qr-export

> Sprint 4 vertical slice. Offline JSON backup of payload+style, plus SVG of the matrix.

## Acceptance criteria

- ✅ User-visible: Home **Export JSON** copies a versioned document (payload + `QrStyle`) to the clipboard
- ✅ Home **Import JSON file** opens SAF and applies a `QrExportDocument` to the editor
- ✅ Empty/invalid JSON decode returns `null`; export never throws on a valid matrix
- ✅ Accessibility: export control has a content description; clipboard result is announced via on-screen status text
- ✅ i18n: `export_json`, `import_json`, `import_json_invalid`, `export_copied`, `export_hint` in `strings.xml`

## Smoke scenario

1. _Given_ the home preview QR is shown
2. _When_ the user taps Export JSON
3. _Then_ the clipboard holds `QrExportJson` with `payloadText` and `style`
4. _When_ the user imports that JSON via SAF
5. _Then_ Home payload and style match the document; invalid files toast and keep the current draft

## Container map

| Layer | Path |
|-------|------|
| Logic | `QrExportJson.kt`, `ExportIntake.kt`, `ShareIntents.kt` |
| View | `EditorActions` **Import JSON file** + `ProductPages` SAF picker |
| Tests | `QrExportTest`, `ExportIntakeTest` |
| Wiring | `ProductPages` OpenDocument launcher |

## Tests

- Automated: yes — `QrExportTest` (JSON round-trip, invalid decode, SVG xmlns/size); `ExportIntakeTest` (apply / reject)
- Coverage: pure encode/decode + SVG header; SAF URI picker is view-only

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist). Instrumented clipboard smoke is not in this slice.
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
data class QrExportDocument(
    val version: Int = 1,
    val payloadText: String,
    val style: QrStyle = QrStyle.DEFAULT,
)

object QrExportJson {
    fun encode(doc: QrExportDocument): String
    fun decode(json: String): QrExportDocument? // blank/invalid → null
}

object QrSvgExporter {
    fun export(matrix: QrMatrix, style: QrStyle = QrStyle.DEFAULT, modulePx: Int = 8): String
}
```

SVG uses square modules and style fg/bg (quiet zone included). Rounded/dot paint is JSON-only in this slice.

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty JSON | `decode` / `ExportIntake.apply` return `null`; Home keeps the current draft and toasts |
| Network timeout | N/A — clipboard and local SAF only |
| Race on clipboard | Single tap; no shared mutable export cache |
| Unhandled JSON exceptions | `decode` catches and returns `null`; URI read is `runCatching` |
