# Feature: editor-feel

> Home stepper feel. One primary CTA, share overflow, human Look labels, IME-safe preview, Snackbar. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Previous + one filled CTA (Next / Save / Share PNG). More export is a menu. Look uses words, not `PILL_H`. Save snackbar above the tab bar
- ✅ Empty payload: Next still works; Save stays `canSave`; Share PNG notices and does not encode
- ✅ Accessibility: Undo/Redo 48dp; preview CD has no payload; More/Advanced expose expanded
- ✅ i18n: `style_labels.xml` plus `editor_step_save` / `export_more` / `tour_home` in en/es/de

## Smoke scenario

1. _Given_ Home is on Content
2. _When_ the user types a URL, taps Next to Save, and saves
3. _Then_ Gallery opens with a snackbar; system Back still steps Previous on Look

## Container map

| Layer | Path |
|-------|------|
| Logic | `StyleLabels.kt`, `EditorSteps.isAdvanced` |
| View | `EditorScreen.kt`, `EditorStepPlace.kt`, `EditorActions.kt` |
| Tests | `StyleLabelsTest`, `EditorStepsTest` |
| Wiring | `ProductPages.notice` via existing `SnackbarHostState` |

## Tests

- Automated: yes — `StyleLabelsTest`, `EditorStepsTest`
- Coverage: every enum has a label res; DEFAULT preset is classic; mixed style is custom; caption/OLED are not advanced

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Named leftovers

- `WidgetConfigActivity` Toast — that Activity has no `QRaftScaffold`
- Last step is not persisted in DataStore (Content default)
- Sideload: both phones `INSTALL_FAILED_UPDATE_INCOMPATIBLE` (debug vs installed signature); never uninstall; vault `[ADB]` still open

## Critique

| Issue | Resolution |
|-------|------------|
| Empty payload | Next enabled; Save `canSave`; Share PNG `notice` |
| Network | N/A |
| Process death | Enum still `Place`; IME compact not saved |
| Unhandled | Preview `runCatching` unchanged |
