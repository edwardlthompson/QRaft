# Feature: ux-gallery-card

> Gallery card is a library card, not a second Home editor. Empty leads with Create, not a vault form. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: open card shows preview, name/tags, Open in editor, export overflow — no Look Advanced, no wallpaper target row
- ✅ Empty: Create a QR + Restore backup (opens filter). Passphrase is not on first paint
- ✅ Accessibility: filter icon says sort and backup
- ✅ i18n: `profiles_restore_backup` / `profiles_filter_open` / `profiles_edit_home` in en/es/de

## Smoke scenario

1. _Given_ Gallery is empty
2. _When_ the user sees the empty state
3. _Then_ there is no passphrase field until they tap Restore backup

## Container map

| Layer | Path |
|-------|------|
| Logic | `GalleryChromeState.openBackup` |
| View | `GalleryCard.kt`, `ProfilesScreen.kt` |
| Tests | `GalleryChromeStateTest` |
| Wiring | Drop in-card save/wallpaper from `ProductProfilesPage` |

## Tests

- Automated: yes — `openBackup` sets `filterOpen`; empty vs miss unchanged
- Coverage: Restore does not require a passphrase field on Empty

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty passphrase | Filter still refuses blank vault via `ProductProfilesPage` |
| In-card save removed | Edit on Home is the path |
| Network | N/A |
