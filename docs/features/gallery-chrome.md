# Feature: gallery-chrome

> Gallery empty vs search-miss, system Back closes the open card, vault passphrase reused on empty, tile TalkBack. Uses `ProductPages.notice` from editor-feel. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: empty gallery shows Create on Home plus Open vault; a search miss says “No matches” only
- ✅ Passphrase required before SAF on empty Open vault (same as Filter)
- ✅ Accessibility: tile image has no CD when the name is shown; Back closes the open card; vault busy is polite live region
- ✅ i18n: `profiles_no_matches` / `profiles_create_home` / `profiles_open_card` in en/es/de

## Smoke scenario

1. _Given_ Gallery has no cards
2. _When_ the user taps Create on Home
3. _Then_ Home opens; opening a card then system Back returns to the grid, not the previous tab

## Container map

| Layer | Path |
|-------|------|
| Logic | `GalleryNav.kt`, `GalleryChromeState.listKind` |
| View | `ProfilesScreen.kt`, `VaultPassphraseRow.kt`, `GalleryFilterPanel.kt` |
| Tests | `GalleryNavTest`, `GalleryChromeStateTest` |
| Wiring | `ProductProfilesPage` already has `onOpenHome` / `notice` |

## Tests

- Automated: yes — `GalleryNavTest` (null vs id), `GalleryChromeStateTest` (empty vs miss vs grid)
- Coverage: Back consume; list kind; passphrase still refused blank by existing vault path

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Named leftovers

- WidgetConfig Toast (no scaffold) — editor-feel
- Sideload signature mismatch — vault `[ADB]` still open; never uninstall

## Critique

| Issue | Resolution |
|-------|------------|
| Nested BackHandler | `GalleryNav.consumeBack`; innermost enabled handler beats `QRaftApp.popNav` |
| Open vault without passphrase | Blank still refuses via `ProductProfilesPage` |
| Search miss showing vault CTA | `listKind` Miss has no vault CTA |
| Network | N/A |
