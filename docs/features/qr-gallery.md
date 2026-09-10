# Feature: qr-gallery

> Sprint 10 session 1. Canonical export documents, card exports, delete, Add Widget.

## Acceptance criteria

- ✅ Save to gallery writes `QrProfile` payload + full `styleJson` (re-render later)
- ✅ Open card: live preview + export actions at the bottom (PNG, SVG, PDF, JSON, Add Widget, Edit)
- ✅ Delete removes DataStore row and `filesDir/gallery/{id}/`
- ✅ Add Widget binds that card id per `appWidgetId`

## Smoke scenario

1. Given a saved gallery card
2. When the user opens it and shares PNG
3. Then export re-encodes from payload+style, not the thumbnail

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/data/...`, `app/.../gallery/GalleryStore.kt` |
| View | `examples/android/app/.../ui/profiles/` |
| Tests | `GalleryStoreTest`, `WidgetPrefsTest` |
| Wiring | `ProductPages` |

## Tests

- Automated: yes — delete sidecar dir; WidgetPrefs two widget ids; export document round-trip

## Fallback validation

- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Critique

| Issue | Resolution |
|-------|------------|
| Empty gallery | Prompt save before Add Widget |
| Missing sidecar | Fail-soft solid background |
| Deleted bound widget | Fail-soft seed Website |
