# Feature: ux-look-fold

> Fold Look jargon: merge preset vs theme, one finder control (not frame+pupil), Advanced switches as `ListItem`. Bigger bet from the UX audit. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: casual Look does not ask for both a named preset and a separate theme when they mean the same look
- ✅ Finder is one control (frame vs pupil folded or pupil defaults with the frame)
- ✅ Advanced OLED / gradient / corner badge rows are `ListItem` + `toggleable` (same as Settings)
- ✅ Accessibility: each Advanced switch is one TalkBack target
- ✅ i18n: any new labels in `style_labels.xml` en/es/de

## Smoke scenario

1. _Given_ Home Look
2. _When_ the user opens Advanced
3. _Then_ they do not see two finder menus plus unlabeled switches

## Container map

| Layer | Path |
|-------|------|
| Logic | `StyleLabels.kt`, `QrStyle` defaults |
| View | `StylePrimary.kt`, `StyleAdvanced.kt` |
| Tests | `StyleLabelsTest`; Advanced row semantics if cheap |
| Wiring | Home Look only |

## Tests

- Automated: yes — pupil follows frame when folded; ListItem toggleable
- Coverage: existing occupancy/style JSON still round-trips

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Null style | `QrStyle` defaults unchanged |
| Saved pupil vs folded UI | Stored JSON keeps both fields; UI writes them together |
| Network | N/A |
| Unhandled | Contrast `Text` stays read-only |
