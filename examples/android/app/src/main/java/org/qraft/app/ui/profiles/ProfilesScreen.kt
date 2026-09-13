package org.qraft.app.ui.profiles

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.gallery.GalleryThumbs
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.data.ProfileSearch
import org.qraft.data.QrProfile

@Composable
fun ProfilesScreen(
    profiles: List<QrProfile>,
    chrome: GalleryChromeState,
    onDelete: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onUndo: () -> Unit,
    onExportJson: (QrProfile) -> Unit,
    onExportPng: (QrProfile) -> Unit,
    onExportSvg: (QrProfile) -> Unit,
    onExportPdf: (QrProfile) -> Unit,
    onAddWidget: (QrProfile) -> Unit,
    onSaveEdit: (QrProfile, String, String, org.qraft.app.editor.EditorDraft, org.qraft.render.QrStyle) -> Unit,
    onEditOnHome: (QrProfile) -> Unit,
    onSetWallpaper: (QrProfile) -> Unit = {},
    onCopyPayload: (QrProfile) -> Unit = {},
    onPrint: (QrProfile) -> Unit = {},
    onZip: (QrProfile) -> Unit = {},
    onBatchPdf: () -> Unit = {},
    onPngSize: (org.qraft.app.share.ExportPngSize) -> Unit = {},
    pngSize: org.qraft.app.share.ExportPngSize = org.qraft.app.share.ExportPngSize.DEFAULT,
    modifier: Modifier = Modifier,
) {
    var passphrase by rememberSaveable { mutableStateOf("") }
    var openId by rememberSaveable { mutableStateOf<String?>(null) }
    val shown = ProfileSearch.sort(ProfileSearch.filter(profiles, chrome.query), chrome.sort)
    val open = profiles.find { it.id == openId }

    // Editing must NOT live inside LazyVerticalGrid — TextField + live preview there crashes.
    if (open != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .highRefreshScroll()
                .verticalScroll(rememberScrollState())
                .padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            GalleryChromePanels(
                chrome = chrome,
                shown = shown.size,
                total = profiles.size,
                passphrase = passphrase,
                onPassphrase = { passphrase = it },
                onBackup = onBackup,
                onRestore = onRestore,
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
            )
            GalleryCard(
                profile = open,
                onSaveEdit = { name, tags, draft, style ->
                    onSaveEdit(open, name, tags, draft, style)
                },
                onEditOnHome = { onEditOnHome(open); openId = null },
                onDelete = { onDelete(open.id); openId = null },
                onExportJson = { onExportJson(open) },
                onExportPng = { onExportPng(open) },
                onExportSvg = { onExportSvg(open) },
                onExportPdf = { onExportPdf(open) },
                onAddWidget = { onAddWidget(open) },
                onSetWallpaper = { onSetWallpaper(open) },
                onCopyPayload = { onCopyPayload(open) },
                onPrint = { onPrint(open) },
                onZip = { onZip(open) },
                onPngSize = onPngSize,
                pngSize = pngSize,
                onClose = { openId = null },
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(SpacingMd),
        horizontalArrangement = Arrangement.spacedBy(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            GalleryChromePanels(
                chrome = chrome,
                shown = shown.size,
                total = profiles.size,
                passphrase = passphrase,
                onPassphrase = { passphrase = it },
                onBackup = onBackup,
                onRestore = onRestore,
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
            )
        }
        if (shown.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.profiles_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(shown, key = { it.id }) { profile ->
                GalleryGridTile(profile = profile, selected = false, onOpen = { openId = profile.id })
            }
        }
    }
}

@Composable
private fun GalleryChromePanels(
    chrome: GalleryChromeState,
    shown: Int,
    total: Int,
    passphrase: String,
    onPassphrase: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onUndo: () -> Unit,
    onBatchPdf: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(
            text = stringResource(R.string.profiles_count, shown, total),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
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
    onUndo: () -> Unit,
    onBatchPdf: () -> Unit,
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
        OutlinedTextField(
            value = passphrase,
            onValueChange = onPassphrase,
            label = { Text(stringResource(R.string.profiles_passphrase)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = { onBackup(passphrase) }) {
                Text(stringResource(R.string.profiles_backup))
            }
            TextButton(onClick = { onRestore(passphrase) }) {
                Text(stringResource(R.string.profiles_restore))
            }
            TextButton(onClick = onUndo) { Text(stringResource(R.string.profiles_undo)) }
            TextButton(onClick = onBatchPdf) { Text(stringResource(R.string.export_batch_pdf)) }
        }
    }
}

@Composable
private fun GalleryGridTile(profile: QrProfile, selected: Boolean, onOpen: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(profile.id, profile.updatedAt) {
        GalleryThumbs.bitmap(context, profile)
    }
    Surface(
        tonalElevation = if (selected) 6.dp else SpacingMd,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(4.dp),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = profile.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                )
            }
            Text(text = profile.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = profile.tags.joinToString().ifBlank { profile.payloadText.take(48) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
