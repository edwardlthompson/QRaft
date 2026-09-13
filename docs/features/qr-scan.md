# Feature: qr-scan

> FOSS offline QR/barcode decode (ZXing). Camera + gallery + history + open/copy/join.

## Acceptance criteria

- User-visible: Scan tab live camera auto-detects (no shutter); torch when available; viewfinder; haptic on new hit
- Gallery pick decodes photo (EXIF + downsample); **Save to gallery** clones payload
- Multi-format ZXing decode (QR preferred, then common 1D/2D); local scan history (cap 50)
- Offline/error: no INTERNET; missing camera → gallery still works; undecodable → status
- Accessibility: buttons labeled; result text selectable
- i18n: `scan_*` keys in `values/strings_scan.xml`

## Smoke scenario

1. Given debug APK on device
2. When user opens Scan, grants camera, points at a URL QR
3. Then payload appears with Open / Copy actions and history entry

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/scan/` (`QrDecoder`, `BarcodeEncoder`, `WifiScanParse`, `ScanActions`) |
| View | `examples/android/app/.../ui/scan/` + `app/scan/ScanHistoryStore.kt` |
| Tests | `scan/src/test/...`, `app/.../ScanHistoryStoreTest.kt` |
| Wiring | `GpRoute.Scan` + `ProductNavBar` + `QRaftScreen` (≤10 lines) |

## Tests

- Automated: yes — `QrDecoderTest`, `BarcodeEncoderTest`, `WifiScanParseTest`, `ScanHistoryStoreTest`
- Coverage: synthetic QR/EAN round-trip; WIFI parse; history push/cap

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Definition of Done

Competitor gap Sequential scan rows; ZXing only (no ML Kit / ads).
