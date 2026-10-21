# Feature: adaptive-shell

> Sprint 13. Shared adaptive layout contract for Android Compose and the web PWA.

## Acceptance criteria

- Breakpoints: compact under 600 · medium 600–839 · expanded 840+ (dp on Android, CSS px on web)
- Home compact: pinned preview above scrollable payload + style controls
- Home medium/expanded: sticky preview beside scrollable controls (~40% / 60%)
- Profiles compact: list then detail navigation; medium+: list | detail panes
- Wallpaper compact: stacked; medium+: preview | size/margin/download
- Accessibility: focus order follows reading order; sticky preview is not a focus trap
- i18n: chrome labels via `nav.*` / `editor.*` (web) and `strings.xml` (Android)

## Smoke scenario

1. Given a viewport wider than 600px (or a tablet WindowSizeClass)
2. When the user opens Home
3. Then the live QR preview stays visible while style controls scroll

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../ui/adaptive/AdaptiveLayout.kt`; web CSS breakpoints in `examples/web/src/style.css` |
| View | Editor / Profiles / Wallpaper screens; `examples/web/src/product/ProductWorkspace.ts` |
| Tests | `QraftWidthClassTest`; web Vitest encode/style round-trip |
| Wiring | Thin — ProductPages / AppShell only |

## Tests

- Automated: yes — `QraftWidthClassTest`; web unit tests for encode/style/safe-zone
- Coverage: width-class mapping + encode/export parity

## Fallback validation

- Command: `python3 scripts/agent-run.py feature-gate --stack multi` (or `scripts/feature-gate.sh --stack multi`)

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty at boundary | Compact path remains the phone layout; unknown size class → Compact |
| Network timeout | N/A — layout only |
| Race | N/A — no async layout state |
| Unhandled exceptions | Preview encode fail-soft with i18n status |

## Notes

- Android widgets / system wallpaper / QS tile remain Android-only.
- Web wallpaper tab downloads a fitted PNG (safe-zone math); does not set OS wallpaper.
