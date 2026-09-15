# Feature: qr-profiles

> Sprint 6. DataStore profiles wired to the editor. No network sync. Cross-phone copy is the encrypted vault in [`qr-profile-vault.md`](qr-profile-vault.md).

## Acceptance criteria

- ✅ Save/load/delete profiles; empty store seeds Personal, Work, and Guest cards
- ✅ Seed URL is `https://example.com` (not a personal site); blank ids are ignored
- ✅ Accessibility: profile names used in TalkBack; Wi-Fi passwords are not used as labels
- ✅ i18n: `profiles_*` keys in `examples/android/app/src/main/res/values/strings.xml`

## Smoke scenario

1. _Given_ the editor shows a URL
2. _When_ the user taps Save Website profile and kills the app
3. _Then_ Profiles lists Website and Load restores payload + style

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/data/src/main/kotlin/org/qraft/data/` |
| View | `examples/android/app/.../profiles/ProfilesScreen.kt` |
| Tests | `ProfileStoreTest`, `ProfileHistoryTest`, `ProfileApplyTest` |
| Wiring | `ProductPages.kt` |
## Tests

- Automated: yes — seed, codec `updatedAt`, history undo, backup JSON import (merge by `updatedAt`; missing sidecar paths cleared)

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Public API (locked)

```kotlin
data class QrProfile(val id: String, val name: String, val payloadText: String, val tags: List<String>, val styleJson: String, val sensitive: Boolean, val updatedAt: Long)
object ProfileHistory { const val MAX = 8; fun push(...); fun undo(...) }
class DataStoreProfileRepository {
  suspend fun seedIfEmpty(); suspend fun exportJson(): String; suspend fun importJson(text: String)
}
object ProfileMerge {
  fun apply(current: Map<String, QrProfile>, incoming: List<QrProfile>): Map<String, QrProfile>
}

```

`importJson` is additive: incoming wins only when `incoming.updatedAt > local.updatedAt`. Equal timestamps keep local. Ids only on this device are never deleted. Missing `imageBackgroundPath` / `logoImagePath` files are cleared on import.

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty at boundary | Blank ids ignored; import of empty JSON is a no-op |
| Network timeout | N/A — on-device DataStore / clipboard / SAF vault |
| Race conditions | Single DataStore.edit per mutation; merge by `updatedAt` |
| Unhandled exceptions | Codec decode catches and returns empty list |
