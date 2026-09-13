# Changelog

All notable changes to **QRaft** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),

and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0](https://github.com/edwardlthompson/QRaft/compare/v1.1.0...v1.2.0) (2026-09-13)


### Added

* **android:** checkpoint Sprint 10-11 editor, gallery, and widget ([a96f8d2](https://github.com/edwardlthompson/QRaft/commit/a96f8d29509bdbeee6ef966cebdd7a7302ed398c))
* **android:** FOSS scan, isolated widgets, barcodes, and gap closure ([4e72834](https://github.com/edwardlthompson/QRaft/commit/4e72834732186755774b8e2a0ff7655c4a9a99e4))


### Fixed

* **android:** assert visible Settings theme value in instrumented UI tests ([4fb1f11](https://github.com/edwardlthompson/QRaft/commit/4fb1f113411a4f0e8c2dcb3c4fe9a89a3720f311))
* **ci:** drop invalid job-level hashFiles for android-release ([63b4947](https://github.com/edwardlthompson/QRaft/commit/63b494700ecebf80636baa626a45575d33ff7cf8))
* **ci:** filter emulator smoke via instrumentation runner args ([70bdb9e](https://github.com/edwardlthompson/QRaft/commit/70bdb9e2d97a2c177a7ff41054aa0fc74ba9fcea))
* **ci:** gate pruned stacks and skip product upgrade sim ([1cc6cc7](https://github.com/edwardlthompson/QRaft/commit/1cc6cc7f549c840eaa9fbc6dede920586947ba06))
* **ci:** restore CodeQL action pins to v4 ([3f64304](https://github.com/edwardlthompson/QRaft/commit/3f643045165995a197f30a9a313e1aedee381f63))
* **ci:** restore Scorecard CodeQL upload-sarif pin to v4 ([4350dde](https://github.com/edwardlthompson/QRaft/commit/4350ddeb0a452c5634d80ac946975a9cbf96c5b5))
* **ci:** run MainActivitySmokeTest on the emulator job ([c9cb2cb](https://github.com/edwardlthompson/QRaft/commit/c9cb2cb8e0c1ea579b413eab09f1d998fc466d1a))
* **ci:** shrink branding rasters and unstick instrumented About/tour ([355844f](https://github.com/edwardlthompson/QRaft/commit/355844ff411c011c61957ba7076df08cbfa30818))
* harden android-only gates and theme package paths ([1cfabb9](https://github.com/edwardlthompson/QRaft/commit/1cfabb935cc228ee12843351d0f5cb4e1e7c2247))

## [Unreleased]

## [0.1.0] - 2026-09-08

### Added

* Bootstrapped QRaft from agent-project-bootstrap (Android, Apache-2.0, offline).
* Multi-module Gradle layout: `:app`, `:core-qr`, `:render`, `:widget`, `:wallpaper`, `:data`.
* Nayuki-backed encode → `QrMatrix` → square rasterize with scannability helpers and unit tests.
* Glance widget receiver scaffold (home + keyguard) and wallpaper safe-zone math.
* Product README, privacy policy, and LineageOS / wallpaper docs.
