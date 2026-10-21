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
