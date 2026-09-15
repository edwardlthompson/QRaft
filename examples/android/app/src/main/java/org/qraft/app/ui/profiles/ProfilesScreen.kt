package org.qraft.app.ui.profiles

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import org.qraft.app.gallery.GalleryNav
import org.qraft.app.gallery.GalleryThumbs
import org.qraft.app.ui.theme.ElevationLevel0
import org.qraft.app.ui.theme.ElevationLevel2
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.app.ui.theme.SpacingXs
import org.qraft.data.ProfileSearch
import org.qraft.data.QrProfile

@Composable
fun ProfilesScreen(
    profiles: List<QrProfile>,
    chrome: GalleryChromeState,
    onDelete: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onSaveVault: (String) -> Unit,
    onOpenVault: (String) -> Unit,
    onUndo: () -> Unit,
    onExportJson: (QrProfile) -> Unit,
    onExportPng: (QrProfile) -> Unit,
    onExportSvg: (QrProfile) -> Unit,
    onExportPdf: (QrProfile) -> Unit,
    onAddWidget: (QrProfile) -> Unit,
    onEditOnHome: (QrProfile) -> Unit,
    onCopyPayload: (QrProfile) -> Unit = {},
    onPrint: (QrProfile) -> Unit = {},
    onZip: (QrProfile) -> Unit = {},
    onBatchPdf: () -> Unit = {},
    vaultBusy: Boolean = false,
    onOpenHome: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var passphrase by remember { mutableStateOf("") }
    var openId by rememberSaveable { mutableStateOf<String?>(null) }
    val shown = ProfileSearch.sort(ProfileSearch.filter(profiles, chrome.query), chrome.sort)
    val open = profiles.find { it.id == openId }
    BackHandler(enabled = GalleryNav.consumeBack(openId)) { openId = null }

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
                onSaveVault = onSaveVault,
                onOpenVault = onOpenVault,
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
                vaultBusy = vaultBusy,
            )
            GalleryCard(
                profile = open,
                onEditOnHome = { onEditOnHome(open); openId = null },
                onDelete = { onDelete(open.id); openId = null },
                onExportJson = { onExportJson(open) },
                onExportPng = { onExportPng(open) },
                onExportSvg = { onExportSvg(open) },
                onExportPdf = { onExportPdf(open) },
                onAddWidget = { onAddWidget(open) },
                onCopyPayload = { onCopyPayload(open) },
                onPrint = { onPrint(open) },
                onZip = { onZip(open) },
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
                onSaveVault = onSaveVault,
                onOpenVault = onOpenVault,
                onUndo = onUndo,
                onBatchPdf = onBatchPdf,
                vaultBusy = vaultBusy,
            )
        }
        when (galleryListKind(profiles.size, shown.size)) {
            GalleryListKind.Empty -> item(span = { GridItemSpan(maxLineSpan) }) {
                GalleryEmptyState(
                    vaultBusy = vaultBusy,
                    onCreateHome = onOpenHome,
                    onRestoreBackup = { chrome.openBackup() },
                )
            }
            GalleryListKind.Miss -> item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.profiles_no_matches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            GalleryListKind.Grid -> items(shown, key = { it.id }) { profile ->
                GalleryGridTile(profile = profile, selected = false, onOpen = { openId = profile.id })
            }
        }
    }
}

@Composable
private fun GalleryEmptyState(
    vaultBusy: Boolean,
    onCreateHome: () -> Unit,
    onRestoreBackup: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(
            text = stringResource(R.string.profiles_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onCreateHome, enabled = !vaultBusy) {
            Text(stringResource(R.string.profiles_create_home))
        }
        TextButton(onClick = onRestoreBackup, enabled = !vaultBusy) {
            Text(stringResource(R.string.profiles_restore_backup))
        }
    }
}

@Composable
private fun GalleryGridTile(profile: QrProfile, selected: Boolean, onOpen: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(profile.id, profile.updatedAt) {
        GalleryThumbs.bitmap(context, profile)
    }
    val openLabel = stringResource(R.string.profiles_open_card)
    Surface(
        tonalElevation = if (selected) ElevationLevel2 else ElevationLevel0,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = openLabel, onClick = onOpen)
            .padding(SpacingXs),
    ) {
        Column(
            modifier = Modifier.padding(SpacingMd),
            verticalArrangement = Arrangement.spacedBy(SpacingXs),
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
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
