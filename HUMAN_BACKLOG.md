# Human Backlog

> Items automation attempted during autonomous `/build` but could not complete. BUILD_PLAN rows stay open until a human finishes them.

| Deferred | Sprint | Owner | Task | Reason |
|----------|--------|-------|------|--------|
| 2026-10-21 | Release | HUMAN | Place `examples/android/qraft-upload.jks` + `keystore.properties`, run `bash scripts/set-android-signing-secrets.sh`, then dispatch Release (`tag=v1.4.0`) and `adb install -r` both phones | Upload keystore not on disk; phones are on upload-signed 1.2.0 — new cert would wipe profiles |
| — | — | — | *(cleared 2026-09-10)* | Prior ADB/HUMAN leftovers passed `attempt-build-plan-row` (device smoke + feature-gate + ZXing decision). Optional peer-phone camera scan remains a manual curiosity only. |
