# Human Backlog

> Items automation attempted during autonomous `/build` but could not complete. BUILD_PLAN rows stay open until a human finishes them.

| Deferred | Sprint | Owner | Task | Reason |
|----------|--------|-------|------|--------|
| 2026-09-08 | Sprint 1 — Core encode + square render | ADB | Install debug APK and scan home-screen sample QR with a phone camera | Debug APK installed on CPH2583; still needs a physical camera scan of the home QR |
| 2026-09-09 | Sprint 5 — Style renderer complete | ADB | Scan styled URL QR (diamond or rounded + OLED) with a phone camera | No automation rule; camera scan needs a second device. Home smoke showed ECC: M and live QR on CPH2583. |
| 2026-09-09 | Sprint 7 — Glance widget product | ADB | Pin widget, tap to full-screen bright QR, scan with a second camera | Pin requires launcher UI; no second camera. Widget prefs per-id unit tests exist. |
| 2026-09-09 | Sprint 8 — Wallpaper set + PNG | ADB | Set lock wallpaper, confirm finders sit inside safe zone; restore prior wallpaper if needed | Lock wallpaper restore needs device confirmation; composer unit tests cover safe-zone blit. |
| 2026-09-09 | Sprint 9 — Export, packs, a11y, F-Droid | ADB | Export PNG of Website QR; optional scan of style-JSON QR | PNG share is chooser UI; SVG/PDF unit tests cover exporters. |
| 2026-09-09 | Sprint 9 — Export, packs, a11y, F-Droid | HUMAN | Optional product smoke after [AUTO] gate pass | attempt-build-plan-row feature-gate.sh path broke on Windows; device smoke on CPH2583 covered Home/share/gallery instead. |
