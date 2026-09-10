# Competitor gap list

Compared 2026-09-08 against static web generators (QR Code Monkey, GenerateOnlineQR / Tembrica-class tools, QRWink) and Android apps (Kodex, QR Widget, F-Droid ShareAsQR / SecScanQR). QRaft stays offline, F-Droid-first, no INTERNET. Dynamic/tracking QR (Bitly, Beaconstac, QR Tiger) are **out of scope**.

## QRaft already has

| Capability | Notes |
|------------|--------|
| Static types | URL, text, Wi-Fi, vCard, email, SMS, phone, crypto |
| Live preview | Pinned at top of Editor + Wallpaper; style on the same Home screen |
| Module / finder style | Square, rounded, dot, diamond, pill, blob; finder frame vs pupil |
| OLED + quiet zone + logo cut-out | Auto ECC H with overlay |
| Profiles | DataStore, search, backup JSON, undo last save |
| Glance widget | Home + LineageOS keyguard, brighten, copy/open, Next |
| Wallpaper | Safe-zone margin, set home/lock/both, SAF PNG |
| Export | PNG, SVG, PDF, JSON, style-JSON QR |
| Privacy | No network permission |

## Gaps worth building (FOSS / offline)

| Gap | Why users expect it | Suggested slice |
|-----|---------------------|-----------------|
| Real logo blit (PNG/SVG asset), not only cut-out | Monkey / Tembrica / Kodex overlay a brand mark | `:render` LogoSpec + occupancy tests |
| Connected blob + true hex/ring finders | Marketing tools advertise distinct patterns | Raster occupancy, keep square fallback |
| Gradients / two-color body | Common on Monkey / QRWink | Optional, warn contrast |
| Decorative frames / CTA labels | QRWink “50+ frames” | Border in leftover, never stretch modules |
| Calendar, geo, WhatsApp, App Store payloads | Web generators list 9–15 types | `QrPayload` + editor dropdown |
| Batch sheet of N saved profiles | Print / events | PDF multi-page from Profiles |
| EPS / high-DPI PNG size picker | Print shops | Export size dropdown |
| Home-screen widget sizes 1×1 / 2×2 / 3×3 + transparent bg | QR Widget | Glance size buckets |
| Scan from camera / gallery | Kodex, SecScanQR | Optional FOSS scanner module, no INTERNET |
| Quick Settings tile | Kodex | Tile → BrightenActivity |
| Material You dynamic color | Kodex | Theme tokens |
| Share-sheet “make this a QR” | ShareAsQR | Intent filter |
| PIN / biometric for sensitive widget | Kickoff leftover | DataStore PIN, not cloud |
| Current Wi-Fi SSID that works on Android 10+ | Needs location at tap | Runtime location, fail-soft copy already exists |

## Deliberately not a gap

| Competitor feature | Why QRaft will not copy |
|--------------------|-------------------------|
| Dynamic QR / scan analytics / short links | Requires network and accounts |
| Firebase, ads, Play-only widgets | FOSS / F-Droid rule |
| Watermarks and paid SVG gates | Export stays free and local |
