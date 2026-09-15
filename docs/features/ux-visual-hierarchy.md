# Feature: ux-visual-hierarchy

> Audit design-system leftovers: preview elevation, Place/Scan titles, filter selected chrome, leftover raw dp. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Home preview uses `ElevationLevel2` (3.dp) so it reads as a card
- ✅ Save step (and Scan, if not already) uses `titleLarge` for the step/page title
- ✅ Search/filter IconButtons use M3 selected container, not tint-only
- ✅ No new raw `8.dp` in Scan camera chrome (`SpacingSm`)
- ✅ Accessibility: selected search/filter still has `stateDescription`
- ✅ i18n: none unless a title key is new

## Smoke scenario

1. _Given_ Home Content
2. _When_ the user looks at the preview and the Save step title
3. _Then_ preview is clearly elevated and Save has a page title

## Container map

| Layer | Path |
|-------|------|
| Logic | none (tokens already exist) |
| View | `EditorScreen.kt`, `EditorStepPlace.kt`, `QRaftScreen.kt`, `ScanCameraPreview.kt` |
| Tests | elevation/token usage where unit-testable; filter selected semantics |
| Wiring | none |

## Tests

- Automated: yes — selected filter semantics still expanded/collapsed; no `8.dp` in ScanCameraPreview
- Coverage: ElevationLevel2 used on Home preview Surface

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Gallery selected used SpacingMd as elevation | Use `ElevationLevel2` only; do not invent 6.dp |
| Network | N/A |
| Unhandled | N/A |
