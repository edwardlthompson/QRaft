# Changelog

All notable changes to **QRaft** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),

and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

* QR style model (`QrStyle`, module/finder shapes, logo cut-out) with rasterizer tests.
* DataStore-backed profile persistence and tag/name search.
* Glance widget bitmap cache + tap-to-brighten action; wallpaper composer with safe-zone blit.
* Offline QR JSON export (clipboard) and SVG exporter with unit tests.
* Home website URL field with live QR preview.
* Product editor shell: Home (payload + style dropdowns), Profiles, and Wallpaper tabs; launch donate/update prompts default off.
* Diamond/pill/blob modules, finder pupil, eye color, and logo cut-out occupancy tests.
* Editor save/load of DataStore Website profiles, history undo, clipboard backup.
* Glance widget shows a live QR; BrightenActivity, copy/open, Next, config, TalkBack without secrets.
* Home editor pins a live QR at the top; style controls live on Home; payload/ECC/shape use dropdowns.
* Automatic ECC by surface; QR color themes, gradients, center icons, and caption band; in-app gallery documents; share-sheet intake.
* Editor draft autosave across process death; unreadable share streams fail-soft.
* Home undo/redo, duplicate to gallery, and New QR.
* Crypto payload scheme picker (bitcoin/ethereum/litecoin/dogecoin/monero).
* Wi-Fi hidden-network flag (`H:true`) on Home.
* vCard organization and URL fields.
* Default sample primary text when switching payload kind.
* Connected blob modules bridge adjacent dark cells.
* True hex finders and hollow ring finders.
* Decorative frames (thin/bold/rounded) in the quiet zone leftover.
* Corner badge occupancy stays off finders and forces overlay ECC.
* Quiet-zone presets 4 / 6 / 8 modules.
* Styled SVG export (dot circles, rounded rects, diamonds).
* PDF export draws the same caption band as PNG (`StyledQrRenderer`).
* Gallery cards can rename and retag; search already matches tags.
* Gallery list sorts by name or newest.
* Optional AES-GCM passphrase wraps gallery clipboard backup.
* Home **Import JSON file** loads a `QrExportDocument` from SAF into the editor.
* Empty gallery seeds Personal, Work, and Guest cards.
* Home-screen widget resizes between 1×1, 2×2, and 3×3.
* Widget background can be transparent so the QR sits on the wallpaper.
* Quick Settings tile **Brighten QR** opens the full-screen bright QR.


## [0.1.0] - 2026-09-08

### Added

* Bootstrapped QRaft from agent-project-bootstrap (Android, Apache-2.0, offline).
* Multi-module Gradle layout: `:app`, `:core-qr`, `:render`, `:widget`, `:wallpaper`, `:data`.
* Nayuki-backed encode → `QrMatrix` → square rasterize with scannability helpers and unit tests.
* Glance widget receiver scaffold (home + keyguard) and wallpaper safe-zone math.
* Product README, privacy policy, and LineageOS / wallpaper docs.
