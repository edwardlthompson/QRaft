package org.qraft.app.ui.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import org.qraft.app.R
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.data.ProfileSearch

@Composable
internal fun GalleryChromePanels(
    chrome: GalleryChromeState,
    shown: Int,
    total: Int,
    passphrase: String,
    onPassphrase: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onSaveVault: (String) -> Unit,
    onOpenVault: (String) -> Unit,
    onUndo: () -> Unit,
    onBatchPdf: () -> Unit,
    vaultBusy: Boolean,
) {
    val busyLabel = stringResource(R.string.profiles_vault_busy)
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(
            text = stringResource(R.string.profiles_count, shown, total),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (vaultBusy) {
            CircularProgressIndicator(
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = busyLabel
                },
            )
        }
        if (chrome.searchOpen) {
            OutlinedTextField(
                value = chrome.query,
                onValueChange = { chrome.query = it },
                label = { Text(stringResource(R.string.profiles_search)) },
                placeholder = { Text(stringResource(R.string.profiles_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (chrome.filterOpen) {
            GalleryFilterPanel(
                sort = chrome.sort,
                onSort = { chrome.sort = it },
                passphrase = passphrase,
                onPassphrase = onPassphrase,
                onBackup = onBackup,
                onRestore = onRestore,
                onSaveVault = onSaveVault,
                onOpenVault = onOpenVault,
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
                vaultBusy = vaultBusy,
            )
        }
    }
}

@Composable
private fun GalleryFilterPanel(
    sort: ProfileSearch.Sort,
    onSort: (ProfileSearch.Sort) -> Unit,
    passphrase: String,
    onPassphrase: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onSaveVault: (String) -> Unit,
    onOpenVault: (String) -> Unit,
    onUndo: () -> Unit,
    onBatchPdf: () -> Unit,
    vaultBusy: Boolean,
) {
    val sortLabels = mapOf(
        ProfileSearch.Sort.NAME to stringResource(R.string.profiles_sort_name),
        ProfileSearch.Sort.NEWEST to stringResource(R.string.profiles_sort_newest),
    )
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        MenuField(
            label = stringResource(R.string.profiles_sort),
            value = sort,
            options = ProfileSearch.Sort.entries.toList(),
            labelOf = { sortLabels.getValue(it) },
            onSelect = onSort,
            modifier = Modifier.fillMaxWidth(),
        )
        VaultPassphraseRow(
            passphrase = passphrase,
            onPassphrase = onPassphrase,
            vaultBusy = vaultBusy,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = { onBackup(passphrase) }, enabled = !vaultBusy) {
                Text(stringResource(R.string.profiles_backup_plaintext))
            }
            TextButton(onClick = { onRestore(passphrase) }, enabled = !vaultBusy) {
                Text(stringResource(R.string.profiles_restore_clipboard))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = { onSaveVault(passphrase) }, enabled = !vaultBusy) {
                Text(stringResource(R.string.profiles_vault_save))
            }
            TextButton(onClick = { onOpenVault(passphrase) }, enabled = !vaultBusy) {
                Text(stringResource(R.string.profiles_vault_open))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = onUndo, enabled = !vaultBusy) {
                Text(stringResource(R.string.profiles_undo))
            }
            TextButton(onClick = onBatchPdf, enabled = !vaultBusy) {
                Text(stringResource(R.string.export_batch_pdf))
            }
        }
    }
}
