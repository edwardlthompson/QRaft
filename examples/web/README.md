<p align="center">
  <img src="../../branding/assets/logo-mark.svg" alt="QRaft" width="64" />
</p>

# QRaft Web (PWA)

FOSS QR creator for PC, Mac, and any browser — Vite + TypeScript PWA with offline service worker, Vitest, Playwright, and Lighthouse CI. Parity with the Android app for encode, style, profiles, and export; wallpaper downloads a fitted PNG. Brand kit: [`branding/BRANDING.md`](../../branding/BRANDING.md).

## Repository layout

```text
examples/web/
  src/
    core-qr/            # Nayuki encode, payloads, ECC, safe-zone
    render/             # Style JSON, Canvas/SVG/PDF, wallpaper compose
    data/               # Profiles, draft, share intake, backup
    product/            # Adaptive Home / Profiles / Wallpaper UI
    locales/en.json     # user-visible strings
  public/               # manifest (Share Target), service worker, icons
  dist/                 # npm run build → GitHub Pages (do not commit)

```

**`docs/` is agent documentation, not this app.** Live site: `examples/web/dist/` via [`.github/workflows/pages.yml`](../../.github/workflows/pages.yml). See [`docs/WEB_PROJECT_LAYOUT.md`](../../docs/WEB_PROJECT_LAYOUT.md).

## Commands

```bash
npm ci
npm test
npm run build
npm run preview
```

## Human setup

Enable **GitHub Pages → Source: GitHub Actions** on the repo (not Deploy from `/docs`). After the first green Pages workflow, the PWA is at `https://<user>.github.io/QRaft/`.
