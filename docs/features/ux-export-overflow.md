# Feature: ux-export-overflow

> Slim Share More. Audit: kitchen-sink overflow (JSON/ZIP/import mixed with PNG) and dead `EditorActions` flags. Duplicate / New / Add widget are not peer Save buttons. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Save step body is name + wallpaper + PNG size; Duplicate / New / Add widget live in More (or a second overflow), not three full-width `TextButton`s
- ✅ More groups takeaway (PNG/SVG/PDF) vs library (JSON/ZIP/import)
- ✅ Home does not compose the unused `EditorActions` flag surface; delete or shrink dead flags
- ✅ Accessibility: overflow items stay labeled; 48dp menu rows
- ✅ i18n: any new group headers in en/es/de

## Smoke scenario

1. _Given_ Home Save
2. _When_ the user opens More
3. _Then_ PNG is with SVG/PDF; JSON/import are separate; Duplicate is not a third primary

## Container map

| Layer | Path |
|-------|------|
| Logic | overflow grouping helper if needed |
| View | `EditorStepFooter.kt`, `EditorStepPlace.kt`, `EditorActions.kt` |
| Tests | overflow item order / flags unused from Home |
| Wiring | `ProductHomeEditor` callbacks only |

## Tests

- Automated: yes — Place body does not include Duplicate as a required slot; Home uses footer overflow
- Coverage: `check-file-limits` on `EditorActions.kt`

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Null callbacks | Keep existing `= {}` defaults until the file is deleted |
| Gallery card overflow | Stay library export; do not re-add wallpaper |
| Network | N/A |
| Unhandled | Style QR confirm unchanged |
