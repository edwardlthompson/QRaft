# Changelog

All notable changes to **QRaft** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),

and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.1](https://github.com/edwardlthompson/QRaft/compare/v1.1.0...v1.1.1) (2026-09-08)


### Fixed

* **ci:** drop invalid job-level hashFiles for android-release ([63b4947](https://github.com/edwardlthompson/QRaft/commit/63b494700ecebf80636baa626a45575d33ff7cf8))
* **ci:** gate pruned stacks and skip product upgrade sim ([1cc6cc7](https://github.com/edwardlthompson/QRaft/commit/1cc6cc7f549c840eaa9fbc6dede920586947ba06))
* **ci:** restore CodeQL action pins to v4 ([3f64304](https://github.com/edwardlthompson/QRaft/commit/3f643045165995a197f30a9a313e1aedee381f63))
* **ci:** restore Scorecard CodeQL upload-sarif pin to v4 ([4350dde](https://github.com/edwardlthompson/QRaft/commit/4350ddeb0a452c5634d80ac946975a9cbf96c5b5))
* harden android-only gates and theme package paths ([1cfabb9](https://github.com/edwardlthompson/QRaft/commit/1cfabb935cc228ee12843351d0f5cb4e1e7c2247))

## [0.1.0] - 2026-09-08

### Added

* Bootstrapped QRaft from agent-project-bootstrap (Android, Apache-2.0, offline).
* Multi-module Gradle layout: `:app`, `:core-qr`, `:render`, `:widget`, `:wallpaper`, `:data`.
* Nayuki-backed encode → `QrMatrix` → square rasterize with scannability helpers and unit tests.
* Glance widget receiver scaffold (home + keyguard) and wallpaper safe-zone math.
* Product README, privacy policy, and LineageOS / wallpaper docs.
