# Feature: ux-tour-tabs

> First-run tour names Scan and Wallpaper so the extra tabs are not leftover chrome. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: tour has a Scan step and a Wallpaper step (or one combined tools step) besides Home and Gallery
- ✅ Widget pin copy stays; total steps stay small (≤5)
- ✅ Accessibility: existing `tour_step_of` live region covers the new count
- ✅ i18n: new keys in en/es/de

## Smoke scenario

1. _Given_ a first-run install
2. _When_ the tour runs
3. _Then_ Scan and Wallpaper are each mentioned before Done

## Container map

| Layer | Path |
|-------|------|
| Logic | `FirstRunTourDialog` body list |
| View | `FirstRunTourDialog.kt` |
| Tests | string resources exist; step count matches bodies |
| Wiring | none |

## Tests

- Automated: yes — body list size matches `tour_step_of` usage; Scan/Wallpaper keys non-blank
- Coverage: en/es/de keys present

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Null bodies | `coerceIn` unchanged |
| Network | N/A |
| Unhandled | N/A |
