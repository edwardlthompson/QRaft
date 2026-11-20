# Build Plan

<!-- remaining-tally -->
**Remaining:** (tally on next sync)
<!-- /remaining-tally -->

Live board. Canonical shape: agent-project-bootstrap `BUILD_PLAN_TEMPLATE.md`.
Finished work: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

**Who:** `AGENT` code · `HUMAN` person · `ADB` device · `AUTO` CI/scripts
**Venue (AGENT only):** `[LOCAL]` This Computer · `[CLOUD]` Cursor Cloud
**State:** 🔲 open · ✅ done · ❌ blocked

Format: `🔲 [AGENT][LOCAL] Short task — scope: path/prefix`.

### Open PRs (synced)

> Auto-managed. Do not hand-edit inside the markers.

<!-- open-prs-sync:begin -->
_No open Dependabot or Release Please PRs._
<!-- open-prs-sync:end -->

### Template gaps (synced)

> Auto-managed. Do not hand-edit inside the markers.

<!-- template-gaps-sync:begin -->
_No template gaps._
<!-- template-gaps-sync:end -->

### Waiting on a person

2. 🔲 [HUMAN] Confirm Pages URL after first green `pages.yml` deploy; update README pitch if needed

### Local agent (This Computer)

Standing queue for This Computer. Rows: `🔲 [AGENT][LOCAL] … — scope: path`.

<!-- local-agent-lane:begin -->
- | 🔲 | Open |
- | ❌ | Blocked (reason after the dash) |
<!-- local-agent-lane:end -->

### Cloud agent (Cursor Cloud)

Standing queue for Cursor Cloud. Rows: `🔲 [AGENT][CLOUD] … — scope: path`.

<!-- cloud-agent-lane:begin -->
_No cloud agent items._
<!-- cloud-agent-lane:end -->

## Child Repo Playbook

### Sprint News — News Bot follow-ups

<!-- parallel_exception: independent news follow-ups; run sequentially -->

1. 🔲 [AGENT] Audit the repo for wfx-loot IOCs — scope: .github/,scripts/ (news P1, 2026-11-14) https://blog.gitguardian.com/wfx-loot-a-ci-secret-stealer-pushed-with-stolen-github-credentials/
2. 🔲 [AGENT] Bump Compose BOM 2026.09.00 → 2026.11.01 (Compose 1.13 / Material3 1.5.0; needs Kotlin 2.2+ and compileSdk 37.1, check AGP) and fix Slider/RangeSlider call sites (`onValueChange` now required); fix 1 Slider/RangeSlider file(s) — scope: examples/android/ (news P2, 2026-11-19) https://developer.android.com/blog/posts/jetpack-compose-november-2026-release
3. 🔲 [AGENT] Bump the Gradle wrapper 9.9.1 → 9.9.2 and commit the regenerated wrapper jar and distributionSha256Sum (4 High security fixes: SFTP host keys, snapshot dependency verification, TestKit temp dir, daemon registry) — scope: examples/android/gradle/wrapper/ (news P2, 2026-11-20) https://docs.gradle.org/9.9.2/release-notes.html

#### Human & device (after automation)

1. 🔲 [ADB] Test lock-screen glance panels with QPR2 widget editing (news P2, 2026-11-13) https://www.androidauthority.com/android-17-qpr2-stable-3733645/
2. 🔲 [HUMAN] Decide on Android developer verification before January 2027: register package name(s) and signing-key SHA-256 in an Android Developer Console account, or add an advanced-flow install note to the README (news P2, 2026-11-20) https://developer.android.com/blog/posts/android-developer-verification-2027


## Archive

Older sprints: [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## Preserved source board

Quoted so no prior task is lost. Prefer the lanes above; do not invent work.

<details>
<summary>Previous board</summary>

# Build Plan

> **QRaft** task board. Finished work → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md). Stuck automation → [`HUMAN_BACKLOG.md`](HUMAN_BACKLOG.md).

| Marker | Meaning |
| ------ | ------- |
| 🔲 | Open |
| ✅ | Done (move to [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md)) |
| ❌ | Blocked (reason after the dash) |
| Label | Who |
| ----- | --- |
| `AGENT` | Cursor agent |
| `HUMAN` | You (product / GitHub) |
| `ADB` | You + device / emulator |
| `AUTO` | CI / scripts |
**Agent order:** finish Sequential `[AGENT]` rows, then Parallel scopes. Status: `python3 scripts/agent-run.py build-sprint-status --json --lane child`.

---

## Child Repo Playbook

> **Sprint 13 — UX audit leftovers** archived in COMPLETED_TASKS.md @ `9511b00`.

History → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

---

## Needs you

No open leftovers.

### Sprint 13 — PWA + adaptive UX (1C / Full)

#### Sequential

1. ✅ [AGENT] Restore `examples/web` + `modules/web`; set stack `multi`; token sync; Pages build smoke
2. ✅ [AGENT] Lock TS parity types + fixtures (payload, style JSON, export, profile, safe-zone)
3. ✅ [AGENT] Adaptive layout contract — `docs/features/adaptive-shell.md`

#### Parallel

| Task | Owner | Scope |
|------|-------|-------|
| Nayuki encode + ECC | AGENT | `examples/web/src/core-qr/**` |
| Canvas/SVG/PDF + style JSON | AGENT | `examples/web/src/render/**` |
| Profiles, backup, draft, share | AGENT | `examples/web/src/data/**` |
| Adaptive PWA product UI | AGENT | `examples/web/src/product/**` |
| Android WindowSizeClass shell | AGENT | `examples/android/app/src/main/java/org/qraft/app/ui/adaptive/**` |

#### Human & device (after automation)

1. ✅ [HUMAN] GitHub Settings → Pages → source **GitHub Actions** (not Deploy from `/docs`)
2. 🔲 [HUMAN] Confirm Pages URL after first green `pages.yml` deploy; update README pitch if needed

---

## Ongoing Maintenance

Monday cron + Dependabot own weekly health (see workflows). Past ship-time checklist items → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

## News inbox (synced)

- 🔲 [AGENT] Audit the repo for wfx-loot IOCs — scope: .github/,scripts/ (news P1, 2026-11-14) https://blog.gitguardian.com/wfx-loot-a-ci-secret-stealer-pushed-with-stolen-github-credentials/
- 🔲 [ADB] Test lock-screen glance panels with QPR2 widget editing (news P2, 2026-11-13) https://www.androidauthority.com/android-17-qpr2-stable-3733645/

</details>
