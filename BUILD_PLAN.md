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

No open sprint rows. History → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

> **Competitor gap closure (FOSS, no ads)** archived in COMPLETED_TASKS.md.

---

## Needs you

No open leftovers.

---

## Ongoing Maintenance

Monday cron + Dependabot own weekly health (see workflows). Past ship-time checklist items → [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).
