# Feature: scan-wallpaper-feel

> Scan Edit on Home matches Gallery Edit on Home (overwrite in-memory draft, no extra confirm, `EditorStep.Content`). Wallpaper uses real buttons. Settings switches are ListItems. No `INTERNET`.

## Acceptance criteria

- ✅ User-visible: Scan **Edit on Home** seeds the Home draft; save-from-scan Snackbar **View** opens Gallery
- ✅ Wallpaper Home / Lock / Both are filled-tonal buttons (not a fire-on-select menu); apply failures `notice`
- ✅ Accessibility: history rows 48dp; viewfinder `scan_preview_cd`; Settings switch rows 48dp ListItems
- ✅ i18n: `scan_edit_home` / `scan_preview_cd` / `snack_view` / `wallpaper_apply_failed` in en/es/de

## Smoke scenario

1. _Given_ a decoded URL on Scan
2. _When_ the user taps Edit on Home
3. _Then_ Home Content shows that payload (same overwrite as Gallery Edit on Home)

## Container map

| Layer | Path |
|-------|------|
| Logic | `ScanHomeSeed.kt`; `GalleryScanClone` no Toast |
| View | `ScanScreen.kt`, `WallpaperTargetRow.kt`, `SettingsScreen.kt` |
| Tests | `ScanHomeSeedTest`; existing `GalleryScanCloneSaveTest` |
| Wiring | `ProductPages` Scan + wallpaper `runCatching` + `notice` |

## Tests

- Automated: yes — `ScanHomeSeedTest` (Wi-Fi / URL kinds match `ProfileApply`); `GalleryScanCloneSaveTest` (no Toast)
- Coverage: seed matches Gallery Edit on Home; Toast removed from clone save

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Named leftovers

- `WidgetConfigActivity` Toast — no `QRaftScaffold`
- Extra confirm before Scan overwrites an unsaved Home draft is out of scope (would fork from Gallery Edit on Home)
- Sideload signature mismatch — vault `[ADB]` still open; never uninstall

## Critique

| Issue | Resolution |
|-------|------------|
| Scan overwrites Home draft | Same as Gallery `onEditOnHome`; named here; no extra dialog |
| Wallpaper apply exception | `runCatching` + `notice(wallpaper_apply_failed)` |
| Duplicate Saved copy | Toast removed; status string not repeated; Snackbar View |
| Network | N/A |
