# Feature: qr-share

> Sprint 9. Offline PNG/SVG/PDF export, style-JSON QR, packs, Fastlane slots.

## Acceptance criteria

- ✅ Share PNG and PDF via FileProvider; copy SVG and JSON to clipboard
- ✅ Empty payload disables export actions; invalid JSON decode stays null
- ✅ Accessibility: export buttons use string resources; TalkBack labels never include Wi-Fi passwords
- ✅ i18n: `export_*` keys in app `strings.xml`

## Smoke scenario

1. _Given_ a Website URL in the editor
2. _When_ the user taps Share PNG
3. _Then_ the system share sheet offers `qraft.png` with no network permission

## Container map

| Layer | Path |
|-------|------|
| Logic | `QrPdfExporter.kt`, `StyleJsonQr`, `QrShare.kt` |
| View | `EditorActions.kt` |
| Tests | `QrExportTest` |
| Packs | `packs/*.json` |
| Metadata | `examples/android/metadata/en-US/` |

## Tests

- Automated: yes — style-JSON payload round-trip, SVG namespace. PDF uses `PdfDocument` (not constructible on the JVM Robolectric path); share is exercised via `QrShare.sharePdf` compile.

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty payload | Editor buttons disabled when `toPayload()` is null |
| Network timeout | N/A — local files / clipboard |
| Race conditions | Cache files overwritten per tap (`qr_export/`) |
| Unhandled exceptions | `encodeOrNull` swallows encode failures |
