# Feature: qr-share-target

> Sprint 10 session 1. Share from browser, contacts, or any app into Home.

## Acceptance criteria

- ✅ `ACTION_SEND` text and vCard, `PROCESS_TEXT`, optional `VIEW` http(s) fill Home draft
- ✅ URL → URL kind; vCard → vCard; mailto/tel/sms/WIFI/geo → matching kind; else Text
- ✅ Empty share stays Home empty preview; oversized text truncated
- ✅ No `INTERNET` permission

## Smoke scenario

1. Given Chrome shares `https://example.com`
2. When QRaft opens
3. Then Home shows a live URL QR

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/app/.../share/ShareIntake.kt` |
| View | Home `EditorScreen` |
| Tests | `ShareIntakeTest` |
| Wiring | `MainActivity` intent filters + `QRaftApp` |

## Tests

- Automated: yes — URL, vCard, plain text, empty, truncate

## Fallback validation

- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Critique

| Issue | Resolution |
|-------|------------|
| Empty extra | null draft; empty preview |
| Huge paste | cap length in ShareIntake |
| Unreadable vCard URI | fail-soft Text |
