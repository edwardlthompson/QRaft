# Feature: ux-motion

> Remaining audit motion, all gated on `ReduceMotion`. Step Crossfade already shipped. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible when motion is on: preview compact/expand height 200ms; Scan payload column fade-in 120ms after a hit; tour body Crossfade
- ✅ Reduce-motion (`animatorDurationScale == 0`): skip those animations; Brighten does not vibrate
- ✅ Snackbar stays the success language (no second checkmark)
- ✅ Accessibility: existing live regions unchanged
- ✅ i18n: none

## Smoke scenario

1. _Given_ animator duration 1
2. _When_ the user steps Home or gets a Scan hit
3. _Then_ the body/payload eases in; with animator 0 it snaps and Brighten is silent

## Container map

| Layer | Path |
|-------|------|
| Logic | `ReduceMotion.kt` (shared with widget if needed) |
| View | `EditorScreen.kt`, `ScanScreen.kt`, `FirstRunTourDialog.kt`, `BrightenChrome.kt` |
| Tests | `ReduceMotionTest`; Brighten skip when scale 0 |
| Wiring | widget Brighten reads the same scale |

## Tests

- Automated: yes — `ReduceMotion.enabled(0f)` skips; haptic helper no-ops when reduced
- Coverage: duration 1 still allows motion helpers to return true

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Null vibrator | Existing null-safe `?.vibrate`; add reduce-motion guard first |
| Race | No new coroutines; Compose animation APIs only |
| Network | N/A |
| Unhandled | Keep Scan haptic on new hit when motion is allowed |
