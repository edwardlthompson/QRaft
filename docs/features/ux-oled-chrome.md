# Feature: ux-oled-chrome

> True-black dark chrome. Audit AAA leftover: product OLED QR ≠ chrome navy `#12121F`. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: dark theme app background is `#000000` (or token `background.dark` true black); surfaces stay readable
- ✅ QR OLED fill on wallpaper/widget is unchanged (already black leftover)
- ✅ Accessibility: on-background contrast stays ≥ 4.5:1
- ✅ i18n: none

## Smoke scenario

1. _Given_ Settings dark theme (not dynamic)
2. _When_ the user opens Home
3. _Then_ chrome is true black, not navy `#12121F`

## Container map

| Layer | Path |
|-------|------|
| Logic | `design-tokens/design-tokens.json` `color.background.dark` |
| View | generated `Color.kt` via `scripts/sync-design-tokens.py` |
| Tests | token value test or color assert |
| Wiring | regen Dimens/Color only |

## Tests

- Automated: yes — dark background token is `#000000`
- Coverage: light theme tokens unchanged

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Dynamic color | True-black applies to the static dark scheme; Material You still follows wallpaper |
| Network | N/A |
| Unhandled | N/A |
