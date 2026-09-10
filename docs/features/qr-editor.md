# Feature: qr-editor

> Sprint 4 product UI shell. Editor is Home (payload + style). Profiles and Wallpaper are the other tabs.

## Acceptance criteria

- ✅ User-visible: Home pins a live QR at the top; payload, ECC, and style are dropdowns/switches below (no FilterChips)
- ✅ Donate/update launch dialogs stay off unless Settings toggle is on (default off)
- ✅ Accessibility: dropdowns use outlined labels; preview has content descriptions; warnings are on-screen text
- ✅ i18n: `editor_*`, `nav_profiles`, `nav_wallpaper`, `settings_nudge_prompts`

## Smoke scenario

1. _Given_ the editor is home
2. _When_ the user picks URL and types a website
3. _Then_ the preview updates and Settings/About stay in the top bar without blocking first launch

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/src/main/java/org/qraft/app/editor/EditorDraft.kt` |
| View | `examples/android/app/src/main/java/org/qraft/app/ui/editor/` |
| Tests | `examples/android/app/src/test/java/org/qraft/app/editor/EditorDraftTest.kt` |
| Wiring | `QRaftScreen` product routes + `ProductPrefs` in `QRaftApp` |

## Tests

- Automated: yes — `EditorDraftTest` (payload mapping, empty reject, auto-H)
- Coverage: pure draft → `QrPayload`; prompt default is false in `ProductPrefs`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
enum class PayloadKind { Url, Text, Wifi, VCard, Email, Sms, Phone, Crypto }

data class EditorDraft(
    val kind: PayloadKind = PayloadKind.Url,
    val primary: String = "https://example.com",
    val secondary: String = "",
    val tertiary: String = "",
    val wifiSecurity: WifiSecurity = WifiSecurity.WPA,
    val ecc: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
)

fun EditorDraft.toPayload(): QrPayload?
fun EditorDraft.effectiveEcc(hasOverlay: Boolean): ErrorCorrectionLevel
```

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty payload | `toPayload()` returns `null`; preview keeps last valid matrix |
| Network timeout | N/A — offline editor |
| Race on draft | Single Compose state; no shared cache |
| Unhandled encode errors | Catch in preview; show warning text |
