# Feature: ux-draft-confirm

> Confirm before Scan or Gallery **Edit on Home** replaces an unsaved Home draft. Named leftover from the UX audit. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: if Home has an unsaved payload, Edit on Home asks before replacing it; empty or already-saved draft proceeds
- ✅ Cancel keeps the current Home step and payload
- ✅ Accessibility: dialog title + primary/secondary actions are 48dp
- ✅ i18n: confirm / keep / replace strings in en/es/de

## Smoke scenario

1. _Given_ Home has a typed URL that is not the last saved gallery card
2. _When_ the user taps Edit on Home from Scan or Gallery
3. _Then_ a dialog offers keep vs replace; Cancel leaves the URL

## Container map

| Layer | Path |
|-------|------|
| Logic | `ScanHomeSeed.kt` / gallery `onEditOnHome` dirty check |
| View | dialog in `ProductPages` or a small `DraftConfirm` composable |
| Tests | unit on dirty vs empty / matching saved id |
| Wiring | Scan + Gallery edit callbacks only |

## Tests

- Automated: yes — dirty when primary differs from last saved; empty is not dirty
- Coverage: matching saved card is not dirty

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|----------|
| Null/empty draft | Empty primary is not dirty; no dialog |
| Race (double tap) | Dialog is modal; second Edit ignored while shown |
| Network | N/A |
| Unhandled | Keep existing seed `runCatching` |
