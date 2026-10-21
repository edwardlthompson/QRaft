# Feature: pwa-creator

> Sprint 13. FOSS PWA on GitHub Pages with creator parity (1C).

## Acceptance criteria

- Payload kinds: URL, text, Wi-Fi, vCard, email, SMS, phone, crypto
- Style: themes, module shapes, caption, OLED shortcut; JSON round-trip with Android `QrStyleJson`
- Profiles in localStorage with search/sort; draft autosave
- Export PNG / SVG / PDF / JSON; import JSON (`QrExportDocument`)
- Wallpaper tab: presets + custom size, safe-zone margin, download PNG
- Web Share Target GET params fill the editor; print CSS hides chrome
- Offline via service worker; no analytics
- Tips in Settings for install PWA and set-as-wallpaper via OS

## Smoke scenario

1. Given the PWA Home tab
2. When the user enters a URL and exports PNG
3. Then a scannable QR downloads without console errors

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/web/src/core-qr/`, `render/`, `data/` |
| View | `examples/web/src/product/ProductWorkspace.ts`, `AppShell.ts` |
| Tests | `encoder.test.ts`, `styleJson.test.ts`, `shareIntake.test.ts` |
| Wiring | `appBootstrap.ts` ≤10 lines for product mount |

## Tests

- Automated: yes — Vitest for encode, style/export JSON, share intake, safe-zone

## Fallback validation

- Command: `python3 scripts/agent-run.py feature-gate --stack web`

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty payload | Defaults match Android `DEFAULT_PRIMARY`; encode errors show i18n status |
| Network timeout | Offline-first; Pages deploy is CI-only |
| Race draft vs import | Import replaces draft then `saveDraft` |
| Unhandled exceptions | Fail-soft empty preview + status string |
