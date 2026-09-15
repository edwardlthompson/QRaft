# Feature: editor-save-share

> Home’s last step is one Save surface: gallery name, set wallpaper, share overflow. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: chips are Content, Look, Save (no separate Share chip). Save body has Home/Lock/Both
- ✅ Footer on Save is Save to gallery plus More (PNG still in More). Next from Look lands on Save
- ✅ Accessibility: wallpaper buttons stay 48dp; empty payload disables set
- ✅ i18n: `tour_home` lists Content, Look, Save (en/es/de)

## Smoke scenario

1. _Given_ Home Look
2. _When_ the user taps Next
3. _Then_ they see name, wallpaper targets, and Save to gallery — not a fourth Share chip

## Container map

| Layer | Path |
|-------|------|
| Logic | `EditorSteps.CHIPS` / `shown` |
| View | `EditorStepPlace.kt`, `EditorStepFooter.kt`, `EditorScreen.kt` |
| Tests | `EditorStepsTest` |

## Tests

- Automated: yes — chips are three steps; `Share` maps to Place; next(Look)=Place
- Coverage: leftover `Share` saveable still previous/back to Look

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|------------|
| Null payload | `WallpaperEmpty.canSet` disables set; Save stays `canSave` |
| Share in rememberSaveable | Keep enum value; `shown(Share)=Place` |
| Network | N/A |
