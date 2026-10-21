# Changelog

All notable changes to **QRaft** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),

and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.0](https://github.com/edwardlthompson/QRaft/compare/v1.3.0...v1.4.0) (2026-10-21)


### Added

* **android:** copy PNG, kind hints, and discard confirms ([a47fb1d](https://github.com/edwardlthompson/QRaft/commit/a47fb1d315c4f1bc3cd9e59e0b0fa23bac78d063))
* **web:** adapt payload fields when kind changes ([49c53aa](https://github.com/edwardlthompson/QRaft/commit/49c53aabf709100823e3bedb10e2fa28192dfd22))
* **web:** Chrome install button, logos, donate, and kind names ([18e7e7a](https://github.com/edwardlthompson/QRaft/commit/18e7e7a16ece5f95f957808e89095bddb1739a60))
* **web:** column Profiles gallery with larger previews ([480a731](https://github.com/edwardlthompson/QRaft/commit/480a73191e0a3ee8c771708a4347f395ee31abe2))
* **web:** expanded desktop Home, style studio, and profile batch export ([8cb348d](https://github.com/edwardlthompson/QRaft/commit/8cb348d2044de878cdc16703cf63b5a269547e1b))
* **web:** Share column, larger profile thumbs, 4K wallpaper default ([17381ea](https://github.com/edwardlthompson/QRaft/commit/17381ea8fa055b5753b60983338c972373c42fea))
* **web:** ship QRaft PWA with adaptive UX on web and Android ([746b473](https://github.com/edwardlthompson/QRaft/commit/746b473806fb3ed6e022b103aec6c24b72e76f53))


### Fixed

* **about:** skip CLI lego strip when stacks are pruned ([93fe78f](https://github.com/edwardlthompson/QRaft/commit/93fe78f76e99d05e7043a2851a71f77045134968))
* **ci:** restore CodeQL action pins to v4 ([86a129f](https://github.com/edwardlthompson/QRaft/commit/86a129f2852dfbc1843dd7992f03195b78ce3ab1))
* **web:** align Playwright e2e with QRaft shell and heading order ([50689b7](https://github.com/edwardlthompson/QRaft/commit/50689b7ff426518ec7db3ef5a8a3e97e4a756a56))
* **web:** export logos, Auto center default, Export menu ([1d0cf91](https://github.com/edwardlthompson/QRaft/commit/1d0cf9169837b6761a9d9821449b63f81171dc0f))
* **web:** Export menu popup and action hierarchy ([261bd93](https://github.com/edwardlthompson/QRaft/commit/261bd93b98b58cb1211a2921e59cd5eeef9b5fea))
* **web:** give Profiles Gallery leftover space so cards do not spill ([b1d4fa7](https://github.com/edwardlthompson/QRaft/commit/b1d4fa7cbdb0f7216d9154f52b7ba8b98c5df54f))
* **web:** keep Profiles Selected pane from overlapping Gallery ([e9a8a4a](https://github.com/edwardlthompson/QRaft/commit/e9a8a4a6753f1a0f87d43a84ff69619550c5e689))
* **web:** polish PWA chrome — logos, tooltips, settings theme ([4d8778b](https://github.com/edwardlthompson/QRaft/commit/4d8778bd10d35d98f4b89ac7f6cb8667a4f3caaf))

## [Unreleased]

## [1.3.0](https://github.com/edwardlthompson/QRaft/compare/v1.2.0...v1.3.0) (2026-09-15)


### Added

* **android:** add vault, Home stepper, and Sprint 13 UX leftovers ([6f43fff](https://github.com/edwardlthompson/QRaft/commit/6f43fff613cb518c01c3be215daabac4f43b78cb))


### Fixed

* **ci:** skip obsolete SDK tools package in setup-android ([5a1ca36](https://github.com/edwardlthompson/QRaft/commit/5a1ca3607f89160bfaf138bced6075fca7f6a41a))

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

## [0.1.0] - 2026-09-08

### Added

* Bootstrapped QRaft from agent-project-bootstrap (Android, Apache-2.0, offline).
* Multi-module Gradle layout: `:app`, `:core-qr`, `:render`, `:widget`, `:wallpaper`, `:data`.
* Nayuki-backed encode → `QrMatrix` → square rasterize with scannability helpers and unit tests.
* Glance widget receiver scaffold (home + keyguard) and wallpaper safe-zone math.
* Product README, privacy policy, and LineageOS / wallpaper docs.
