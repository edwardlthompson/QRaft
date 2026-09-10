# Build Plan

> Prioritized task board with owner labels. **Completed sprints:** `COMPLETED_TASKS.md`.

## Owner Label Legend

| Label   | Owner           | When to use                                                |
| ------- | --------------- | ---------------------------------------------------------- |
| `AGENT` | Cursor Agent    | Code, docs, scaffolding, tests, CI config                  |
| `HUMAN` | Human developer | Approvals, credentials, GitHub settings, product decisions |
| `ADB`   | Human (Android) | Android SDK, emulator/device testing, F-Droid submission   |
| `AUTO`  | CI/scripts/bots | GitHub Actions, Dependabot, pre-commit, update checker     |
## Status markers

Use **emoji markers** (not `- [ ]` GitHub checkboxes) so task state reads clearly in Markdown source and Preview. **Applies repo-wide** — `BUILD_PLAN.md`, module checklists, PR template, feature specs, and security triage.

| Marker | State   | Agent action                                                          |
| ------ | ------- | --------------------------------------------------------------------- |
| 🔲     | Open    | Default for new tasks; work or leave queued                           |
| ✅      | Done    | Replace 🔲 when complete; archive sprint rows to `COMPLETED_TASKS.md` |
| ❌      | Blocked | Replace 🔲 when blocked; add brief reason after the description       |
**Task format:** `🔲 [OWNER] Description` · done: `✅ [OWNER] Description` · blocked: `❌ [OWNER] Description — reason`

```bash
grep '\[AGENT\]' BUILD_PLAN.md
grep '\[HUMAN\]' BUILD_PLAN.md
grep '\[ADB\]' BUILD_PLAN.md
grep '\[AUTO\]' BUILD_PLAN.md

```

**Agent rule:** Execute all `[AGENT]` **Sequential** items first, then dispatch **Parallel** agents with isolated file scopes (`docs/PARALLEL_AGENT_SCOPES.md`). Shared schema/types are Sequential-only.

### Parallel dispatch protocol (orchestrator)

| Step | Action                                                                                                                                                                     |
| ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | Finish all `[AGENT]` **Sequential** items for the active sprint/feature (shared schema/types locked)                                                                       |
| 2    | **Discover** parallelizable work using the decomposition checklist below; add Parallel table rows with non-overlapping ``path/**`` scopes                                  |
| 3    | Run `bash scripts/plan-parallel-dispatch.sh` → read **agent_count**                                                                                                        |
| 4    | If `agent_count >= 2`, run `/scope` (auto Task dispatch); if `1`, execute inline; if `0`, run `--suggest` and expand the Parallel table (or document `parallel_exception`) |
| 5    | Sequential owner merges results, runs `watch-agent-gates.sh`, updates BUILD_PLAN (Parallel agents never edit BUILD_PLAN)                                                   |
**Decomposition checklist** (apply before finalizing Sequential items):

| Heuristic                     | Split into Parallel agents                                                                  |
| ----------------------------- | ------------------------------------------------------------------------------------------- |
| Multi-stack repo              | One agent per active module (`examples/{stack}/`**)                                         |
| Feature container (Sprint 2+) | Agent A: pure logic + unit tests; Agent B: view/Composable + i18n                           |
| Tests vs production code      | Separate `**/*.test.*`, `e2e/**`, `androidTest/**` when paths do not overlap implementation |
| Docs vs code                  | Agent A: `examples/**`; Agent B: `docs/**`, `modules/**`, `.cursor/rules/**`                |
| CI/gates vs app code          | Agent A: `scripts/**`, `.github/workflows/**`; Agent B: stack example tree                  |
**Default rule:** If a Sequential `[AGENT]` item touches two or more non-overlapping directory prefixes, **split it** — leave only schema-lock work Sequential.

**Planning (Plan Mode):** Every BUILD_PLAN proposal must include `### Parallelization` with `agent_count_target`, decomposition table, and dry-run from `plan-parallel-dispatch.sh`. Run `check-build-plan-parallel.sh` before human approval.

**Autonomous `/build`:** Runs all `[AGENT]`/`[AUTO]` and Parallel work first, then attempts the grouped **Human & device (after automation)** section via `scripts/attempt-build-plan-row.sh`. Success marks ✅; failure appends `HUMAN_BACKLOG.md` and continues — never halts on human labels. Humans review the grouped section (and backlog) after automation finishes. Status: `bash scripts/build-sprint-status.sh --json`.

> **Template maintainer:** **M47** archived. HUMAN leftovers that still need a person: CII, optional Ollama, Android SDK. Last ship **v1.0.0**. **Child repos:** copy the playbook.

---

## Template Maintainer — Active Board

> **v1.0.0** published @ `3dae768`. **M47** archived in COMPLETED_TASKS.md @ `d573863`. **M46** AGENT/AUTO archived in COMPLETED_TASKS.md @ `6229822`. Closed HUMAN leftovers archived in COMPLETED_TASKS.md @ `c61d249`. Still open: CII login, optional Ollama, Android SDK licenses. **M45** and **M44** archived in COMPLETED_TASKS.md. **v0.25.0** archived in COMPLETED_TASKS.md @ `7670444`. **v0.24.0** archived in COMPLETED_TASKS.md @ `c0f0dee`.

### M47 — Cline-first onboarding + Golden Path navigation

> **M47** archived in COMPLETED_TASKS.md @ `d573863`.

### M46 leftovers (human only)

1. 🔲 [HUMAN] P2: CII Best Practices checklist (login + public badge)

### M43 leftovers (human/device)

1. 🔲 [HUMAN] Optional: install Ollama and point Cursor Models at `http://127.0.0.1:11434/v1` (`docs/LOCAL_MODELS.md`)
2. 🔲 [ADB] Optional: Android SDK licenses + first AVD (`/emulator` or `just android-instrumented`)

---

## Child Repo Playbook (copy after Use this template)

> Init scripts, feature docs (`docs/features/_template.md`), and About + Settings exemplars ship with the template. Mirror the Sequential + Parallel lane structure from Sprint M9 when customizing.

### CRITICAL NOTES (phase transitions)

When **Sprint 0** ends: stop re-reading `docs/INITIALIZATION_PROMPT.md` as the daily driver. `/feature` expects a copied `docs/features/{name}.md` from `_template.md`, a locked public API, then Parallel logic/view slices. Copy `scratchpad.md.example` → `scratchpad.md` (gitignored) and **reset** it on sprint/phase change — do not replace `AGENT_MEMORY.md`.

### Sprint 0 — Template Customization

> **Sprint 0** archived in COMPLETED_TASKS.md @ `d573863`.
<!-- parallel_exception: archived -->

### Sprint 1 — Core encode + square render

> **Sprint 1** archived in COMPLETED_TASKS.md @ `d573863` (NOTICE confirmed). ADB scan remains open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. 🔲 [ADB] Install debug APK and scan home-screen sample QR with a phone camera

### Sprint 2 — Custom renderer + profiles

> **Sprint 2** archived in COMPLETED_TASKS.md @ `d573863`.
<!-- parallel_exception: archived -->

### Sprint 3 — Widget + wallpaper

> **Sprint 3** archived in COMPLETED_TASKS.md @ `d573863`.
<!-- parallel_exception: archived -->

### Sprint 4+ — Incremental Features

> **qr-export** and **qr-editor** sequential archived in COMPLETED_TASKS.md. Sprint 5–9 AGENT work archived; ADB/HUMAN leftovers stay open.
<!-- parallel_exception: archived -->

### Sprint 5 — Style renderer complete

> **Sprint 5** AGENT rows archived in COMPLETED_TASKS.md. ADB scan remains open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. 🔲 [ADB] Scan styled URL QR (diamond or rounded + OLED) with a phone camera

### Sprint 6 — Profiles persistence wired

> **Sprint 6** AGENT rows archived in COMPLETED_TASKS.md. HUMAN reload check remains open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. ✅ [HUMAN] Save Website profile, kill app, confirm it reloads

### Sprint 7 — Glance widget product

> **Sprint 7** AGENT rows archived in COMPLETED_TASKS.md. ADB widget scan remains open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. 🔲 [ADB] Pin widget, tap to full-screen bright QR, scan with a second camera

### Sprint 8 — Wallpaper set + PNG

> **Sprint 8** AGENT rows archived in COMPLETED_TASKS.md. ADB lock-wallpaper check remains open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. 🔲 [ADB] Set lock wallpaper, confirm finders sit inside safe zone; restore prior wallpaper if needed

### Sprint 9 — Export, packs, a11y, F-Droid

> **Sprint 9** AGENT rows archived in COMPLETED_TASKS.md. ADB export scan and optional HUMAN smoke remain open.
<!-- parallel_exception: archived -->

#### Human & device (after automation)

1. 🔲 [ADB] Export PNG of Website QR; optional scan of style-JSON QR
2. 🔲 [HUMAN] Optional product smoke after [AUTO] gate pass

### Sprint 10 — Style, gallery, share (session 1)

> Schema lock then S1 vertical slices. Queued allideas stay 🔲 in Sprint 11–12.
<!-- parallel_exception: sequential schema lock; remaining 1-80 are later sessions -->

#### Sequential

1. ✅ [AGENT] Lock `EccPolicy`, `QrStyle` v2, `ShareIntake`; specs `qr-style-plus`, `qr-gallery`, `qr-share-target`
2. ✅ [AGENT] Auto ECC + readout; remove ECC dropdown (#1–2)
3. ✅ [AGENT] Raster themes, gradient, image bg, center inlay, caption taller-not-wider (#13–18, 49)
4. ✅ [AGENT] Home StyleControls, named save, validation (#4, 8, 23, 25–26)
5. ✅ [AGENT] Gallery documents, card exports, delete, Add Widget, per-widget id (#29–33, 39–40)
6. ✅ [AGENT] Share-sheet intake into Home (#68)

### Sprint 11 — Queued editor, style, gallery, widget

<!-- parallel_exception: queued allideas after Sprint 10 S1 lock -->

1. ✅ [AGENT] #3 Draft autosave across process death
2. ✅ [AGENT] #5 Undo/redo beyond last profile save
3. ✅ [AGENT] #6 Duplicate current QR
4. ✅ [AGENT] #7 Clear / new QR
5. ✅ [AGENT] #9 Crypto scheme picker
6. ✅ [AGENT] #10 Wi-Fi hidden-network flag
7. ✅ [AGENT] #11 vCard org + URL fields
8. ✅ [AGENT] #12 Default primary per payload kind
9. ✅ [AGENT] #19 True connected-blob modules
10. ✅ [AGENT] #20 True hex / ring finders
11. ✅ [AGENT] #21 Decorative frames
12. ✅ [AGENT] #22 Corner badge occupancy
13. ✅ [AGENT] #24 Quiet-zone presets 4/6/8
14. ✅ [AGENT] #27 Styled SVG export
15. ✅ [AGENT] #28 Styled PDF + caption
16. ✅ [AGENT] #34 Gallery rename / tags
17. ✅ [AGENT] #35 Gallery sort
18. ✅ [AGENT] #36 Encrypted local backup
19. ✅ [AGENT] #37 Import QrExportDocument from SAF
20. ✅ [AGENT] #38 Seed Personal/Work/Guest profiles
21. ✅ [AGENT] #41 Widget sizes 1x1 / 2x2 / 3x3
22. ✅ [AGENT] #42 Transparent widget background
23. 🔲 [AGENT] #43 Quick Settings tile to Brighten
24. 🔲 [AGENT] #44 PIN / biometric for sensitive widget
25. 🔲 [AGENT] #45 Per-widget caption from gallery card
26. 🔲 [AGENT] #46 Keyguard vs home pin helper
27. 🔲 [AGENT] #47 Widget cache refresh on gallery edit
28. 🔲 [AGENT] #48 TalkBack payload kinds without secrets

### Sprint 12 — Queued wallpaper, export, payloads, scan, polish

<!-- parallel_exception: queued allideas after Sprint 10 S1 lock -->

1. 🔲 [AGENT] #50 Wallpaper from gallery card
2. 🔲 [AGENT] #51 Lock vs home overlay preview
3. 🔲 [AGENT] #52 Restore previous wallpaper (code)
4. 🔲 [AGENT] #53 Dark/light wallpaper pair
5. 🔲 [AGENT] #54 PNG size picker
6. 🔲 [AGENT] #55 Batch PDF of gallery cards
7. 🔲 [AGENT] #56 Share PNG with caption band
8. 🔲 [AGENT] #57 Copy payload text
9. 🔲 [AGENT] #58 System Print helper
10. 🔲 [AGENT] #59 ZIP document + sidecars
11. 🔲 [AGENT] #60 Calendar VEVENT payload
12. 🔲 [AGENT] #61 Geo payload
13. 🔲 [AGENT] #62 WhatsApp wa.me static URL
14. 🔲 [AGENT] #63 App store / F-Droid URL
15. 🔲 [AGENT] #64 Mastodon / Matrix URL presets
16. 🔲 [AGENT] #65 MeCard payload
17. 🔲 [AGENT] #66 FaceTime URL payloads
18. 🔲 [AGENT] #67 Current Wi-Fi SSID with runtime location
19. 🔲 [AGENT] #69 Camera scan (FOSS, offline)
20. 🔲 [AGENT] #70 Decode QR from gallery image
21. 🔲 [AGENT] #71 Scan actions open/copy/join
22. 🔲 [AGENT] #72 Deep link qraft://profile/{id}
23. 🔲 [AGENT] #73 Material You dynamic color
24. 🔲 [AGENT] #74 Settings theme dropdown
25. 🔲 [AGENT] #75 TalkBack on editor dropdowns
26. 🔲 [AGENT] #76 Font scale / large preview
27. 🔲 [AGENT] #77 German / Spanish strings
28. 🔲 [AGENT] #78 First-run Home to Gallery to Widget tour
29. 🔲 [AGENT] #79 App shortcuts New / last card
30. 🔲 [AGENT] #80 Edge-to-edge gallery grid

---

## Ongoing Maintenance (recurring)

> **Template maintainer:** `bash scripts/run-maintainer-gates.sh` weekly (omit `--quick` for full CI wait).

### Weekly

- 🔲 [AUTO] `cursor-feature-radar.sh` (non-blocking; artifact in weekly-health-check)
- 🔲 [AUTO] `check-security-triage.sh --wait-ci 300` (Dependabot + CI + Scorecard)
- 🔲 [AGENT] `/update-deps` locally; triage leftover Dependabot PRs and Scorecard SARIF
- 🔲 [AUTO] CI matrix + Repo Hygiene + Feature Gate green on `main`

### Monthly

- 🔲 [AUTO] `simulate-template-upgrade.sh` (also in `weekly-health-check.yml`)
- 🔲 [AUTO] `check-license-compliance.sh` + SBOM on latest release
- 🔲 [AGENT] Review Dependabot auto-merge PRs (KB-007)

### Pre-release (every version)

- 🔲 [AUTO] `pre-release-gate.sh --local` before push; full `pre-release-gate.sh` + `run-maintainer-gates.sh` after (`verify-branch-protection.sh`)
- 🔲 [AUTO] Release Please PR merged; CHANGELOG + manifest bumped

### Human (after automation)

> Product approvals after automated pre-release gates pass.

- 🔲 [HUMAN] Approve release tag when product-ready
- 🔲 [HUMAN] Quarterly Cursor feature radar backlog review (next due 2026-11-15; last pass 2026-08-15)

---

## Archived Sprints

| Sprint                                                            | Status   | Archive                          |
| ----------------------------------------------------------------- | -------- | -------------------------------- |
| Sprint 5–9 — Remaining product (style/widget/wallpaper/share) | AGENT archived; ADB/HUMAN open | `COMPLETED_TASKS.md` |
| Sprint 4 — qr-editor + qr-export                              | Complete | `COMPLETED_TASKS.md` |
| M47 — Cline-first + Golden Path nav                               | Complete | `COMPLETED_TASKS.md` @ `d573863` |
| Sprint 3 — Widget + wallpaper                                     | Complete | `COMPLETED_TASKS.md` @ `d573863` |
| Sprint 2 — Custom renderer + profiles                             | Complete | `COMPLETED_TASKS.md` @ `d573863` |
| Sprint 1 — Core encode + square render                            | Complete | `COMPLETED_TASKS.md` @ `d573863` |
| Sprint 0 — Template Customization                                 | Complete | `COMPLETED_TASKS.md` @ `d573863` |
| HUMAN leftover automation                                         | Complete | `COMPLETED_TASKS.md` @ `c61d249` |
| M46 — /allideas template backlog                                  | Complete | `COMPLETED_TASKS.md` @ `6229822` |
| M45 — /ideas round 2                                             | Complete | `COMPLETED_TASKS.md`             |
| M44 — /ideas ship hygiene                                        | Complete | `COMPLETED_TASKS.md`             |
| v0.25.0 Local-first deps and resource packing                     | Complete | `COMPLETED_TASKS.md` @ `7670444` |
| M43 — Local resource packing                                      | Complete | `COMPLETED_TASKS.md`             |
| M42 — Local-first dependency updater                              | Complete | `COMPLETED_TASKS.md`             |
| v0.24.0 Privacy-first GitHub feedback                             | Complete | `COMPLETED_TASKS.md` @ `c0f0dee` |
| M41 — Privacy-first GitHub crash and feedback                     | Complete | `COMPLETED_TASKS.md`             |
| v0.23.0 Continuum donations and updates                           | Complete | `COMPLETED_TASKS.md` @ `b85cd74` |
| M40 — Donations and updates (Continuum method)                    | Complete | `COMPLETED_TASKS.md`             |
| v0.22.0 Android same-resolution high-refresh                      | Complete | `COMPLETED_TASKS.md` @ `9a18276` |
| M39 /ideas Windows PATH + ship hygiene                            | Complete | `COMPLETED_TASKS.md`             |
| v0.21.0 Windows PATH + Unreleased fold                            | Complete | `COMPLETED_TASKS.md` @ `1525cd6` |
| M38 /ideas ship-hardening                                         | Complete | `COMPLETED_TASKS.md`             |
| Coach / M37 / M36 (stale ✅ active-board rows)                     | Complete | `COMPLETED_TASKS.md`             |
| v0.20.0 first-run backlog + Windows upgrade-sim                   | Complete | `COMPLETED_TASKS.md` @ `b570f07` |
| v0.19.0 portable first-run release                                | Complete | `COMPLETED_TASKS.md` @ `2bef8ac` |
| v0.18.3 Compose BOM release                                       | Complete | `COMPLETED_TASKS.md` @ `013e688` |
| v0.18.2 Scorecard + Dependabot release                            | Complete | `COMPLETED_TASKS.md` @ `7d46e68` |
| M35 HUMAN — Scorecard + Dependabot + radar                        | Complete | `COMPLETED_TASKS.md`             |
| v0.18.1 Windows Python resolver release                           | Complete | `COMPLETED_TASKS.md` @ `fe80fea` |
| M35 — Audit 2026-08-15                                            | Complete | `COMPLETED_TASKS.md`             |
| v0.18.0 prior-art thin steals release                             | Complete | `COMPLETED_TASKS.md` @ `3f0b5a3` |
| M34 — Prior-art thin steals                                       | Complete | `COMPLETED_TASKS.md`             |
| v0.17.0 branding kit release                                      | Complete | `COMPLETED_TASKS.md` @ `701cd24` |
| v0.15.2 release                                                   | Complete | `COMPLETED_TASKS.md` @ `634d06d` |
| v0.15.0 release                                                   | Complete | `COMPLETED_TASKS.md` @ `2e010ae` |
| M33 — Cursor 3.9–3.11 + local-first compute                       | Complete | `COMPLETED_TASKS.md` @ `5d2d129` |
| v0.14.1 release                                                   | Complete | `COMPLETED_TASKS.md` @ `a6c6be1` |
| M32 — Audit 2026-07-12                                              | Complete | `COMPLETED_TASKS.md` @ `e532c20` |
| v0.14.0 release                                                   | Complete | `COMPLETED_TASKS.md` @ `4b94298` |
| v0.13.2 release                                                   | Complete | `COMPLETED_TASKS.md` @ `ff8e4e6` |
| M31 — Audit 2026-07-01                                            | Complete | `COMPLETED_TASKS.md`             |
| M30 — Cursor FOSS integration + feature radar                     | Complete | `COMPLETED_TASKS.md` @ `508a541` |
| M19–M29 — Cursor modes, batch commands, maintain, v0.11.0 release | Complete | `COMPLETED_TASKS.md`             |
| v0.10.0 release (`36a02e4`)                                       | Complete | `COMPLETED_TASKS.md`             |
| M5–M18 maintainer sprints (seq + P2)                              | Complete | `COMPLETED_TASKS.md` @ `d6b92a2` |
| Child Sprint 2 starter scaffold                                   | Complete | `COMPLETED_TASKS.md`             |
| v0.9.0 release (`fd699bc`)                                        | Complete | `COMPLETED_TASKS.md`             |
