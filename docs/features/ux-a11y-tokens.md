# Feature: ux-a11y-tokens

> Scan uses design tokens. Search/filter announce expanded. Settings switch rows are one toggle. Wallpaper empty cannot set. Widget config errors stay on-screen. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: empty wallpaper shows “enter URL or text” and set buttons are off
- ✅ Widget config: pick-required is in-layout `Text`, not a Toast
- ✅ Accessibility: search/filter `stateDescription`; Settings `toggleable`; tour “n of m”; Scan status live region
- ✅ i18n: `tour_step_of` in en/es/de

## Smoke scenario

1. _Given_ Home draft payload is empty
2. _When_ the user opens Wallpaper
3. _Then_ set Home/Lock/Both are disabled and no apply snack is needed

## Container map

| Layer | Path |
|-------|------|
| Logic | `ReduceMotion`, `WallpaperEmpty`, `WidgetConfigPick` |
| View | `ScanScreen`, `QRaftScreen`, `SettingsScreen`, `FirstRunTourDialog`, `WallpaperScreen`, `WidgetConfigActivity`, `EditorScreen` |
| Tests | `ReduceMotionTest`, `WallpaperEmptyTest`, `WidgetConfigPickTest` |

## Tests

- Automated: yes — reduce-motion scale 0; empty draft cannot set wallpaper; null widget pick shows error
- Coverage: no new Toast in `:app`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|------------|
| Null payload | `WallpaperEmpty.canSet` disables buttons |
| Motion vs reduce-motion | `Crossfade` skipped when animator duration is 0 |
| Network | N/A |
