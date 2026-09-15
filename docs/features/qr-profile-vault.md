# Feature: qr-profile-vault

> Encrypted gallery vault via the system file picker (SAF). No `INTERNET`. Drive/Nextcloud/USB are destinations the user picks; QRaft never talks to those apps.

## Acceptance criteria

- ✅ User-visible: Gallery **Save vault** writes `QRAFT2:` AES-GCM to a user-picked file (`qraft-vault.qraft`); **Open vault** merges that file into local profiles
- ✅ Passphrase is required for vault (blank refuses and does not open the picker); clipboard backup stays optional/plaintext
- ✅ Import is additive by `id` + `updatedAt` (newer wins; equal keeps local; missing ids are never deleted)
- ✅ Missing `imageBackgroundPath` / `logoImagePath` on this device are cleared; photo/logo binaries are not in the vault
- ✅ Accessibility: passphrase field is masked; TalkBack uses the field label, not the secret
- ✅ i18n: `profiles_vault_*` keys in `values/strings.xml`, `values-es/`, `values-de/`

## Smoke scenario

1. _Given_ both phones run this build (`adb install -r`, do not uninstall) and Gallery has cards
2. _When_ the user sets a passphrase, taps **Save vault**, and picks Drive, Nextcloud, or USB
3. _Then_ the toast reports the file was saved (not “synced to Drive”)
4. _When_ the other phone taps **Open vault**, picks the same file, and enters the same passphrase
5. _Then_ new cards appear, newer edits replace older ones, local-only cards stay, widgets refresh, and a random PDF toasts invalid without changing data

## Container map

| Layer | Path |
|-------|------|
| Logic | `BackupCrypto.kt`, `ProfileMerge.kt`, `VaultFile.kt`, `GalleryRestore.kt` |
| View | `ProfilesScreen` / `GalleryFilterPanel` Save vault + Open vault |
| Tests | `BackupCryptoTest`, `ProfileMergeTest`, `ProfileStoreTest`, `VaultFileTest` |
| Wiring | `ProductProfilesPage` CreateDocument / OpenDocument (not `MainActivity`) |

## Tests

- Automated: yes — `BackupCryptoTest` (QRAFT2 round-trip, QRAFT1 unwrap, blank `wrapVault`), `ProfileMergeTest` / `ProfileStoreTest` (updatedAt, missing sidecar paths), `VaultFileTest` (2 MiB cap)
- Coverage: crypto + merge plus bounded read; SAF picker is view-only

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist). Two-phone Drive/USB smoke is `[ADB]`.
- Command: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`

## Public API (locked)

```kotlin
object BackupCrypto {
  fun wrap(plain: String, passphrase: String): String
  fun unwrap(blob: String, passphrase: String): String?
  fun wrapVault(plain: String, passphrase: String): String?
  fun looksLikeBackup(blob: String): Boolean
}
object ProfileMerge {
  fun apply(current: Map<String, QrProfile>, incoming: List<QrProfile>): Map<String, QrProfile>
  fun wonIds(current: Map<String, QrProfile>, incoming: List<QrProfile>): Set<String>
}
```

## Critique

| Issue | Resolution |
|-------|------------|
| Null/empty passphrase | `wrapVault` returns null; UI refuses and toasts; `BackupCryptoTest` |
| Network timeout | N/A — SAF only; toast is saved-to-picked-file |
| Two-device race | `ProfileMerge` by `updatedAt`; equal keeps local; `ProfileStoreTest` |
| Unhandled crypto/IO | `runCatching`; import only after unwrap+decode succeed |
| Clock skew | `updatedAt` is local `currentTimeMillis`; no NTP; re-save on the lagging phone |
| Stale thumbs/widgets | `GalleryStore.delete` for won ids; `WidgetRefresh.afterGalleryEdit` |
| Photo/logo sidecars | Sanitize missing paths; zip-sidecars not in this slice |

## Notes

- Vault requires this build on **both** phones. Clipboard stays `QRAFT1:` so 1.2.0 can still restore clipboard JSON.
- Follow-ups (safe to ship without): persist last vault URI, WebDAV, zip sidecars, delete tombstones.
- After AGENT step: `python3 scripts/agent-run.py watch-agent-gates --once --autofix --scope auto`
