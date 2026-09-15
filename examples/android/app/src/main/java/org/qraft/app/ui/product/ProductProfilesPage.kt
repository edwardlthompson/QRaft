package org.qraft.app.ui.product

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.gallery.GalleryStore
import org.qraft.app.gallery.GalleryVault
import org.qraft.app.share.BatchPdfExport
import org.qraft.app.share.ExportPngSize
import org.qraft.app.share.QrPrint
import org.qraft.app.share.QrShare
import org.qraft.app.share.QrZipExport
import org.qraft.app.share.WidgetPin
import org.qraft.app.ui.profiles.GalleryChromeState
import org.qraft.app.ui.profiles.ProfilesScreen
import org.qraft.data.BackupCrypto
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.ProfileHistory
import org.qraft.data.ProfileSnapshot
import org.qraft.data.QrProfile
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

@Composable
internal fun ProductProfilesPage(
    profiles: List<QrProfile>,
    history: List<ProfileSnapshot>,
    pngSize: ExportPngSize,
    onHistory: (List<ProfileSnapshot>) -> Unit,
    onDraftStyle: (EditorDraft, String, String) -> Unit,
    onOpenHome: () -> Unit,
    context: Context,
    scope: CoroutineScope,
    repo: DataStoreProfileRepository,
    clip: (String) -> Unit,
    notice: (Int) -> Unit,
    refresh: suspend () -> Unit,
    chrome: GalleryChromeState,
    modifier: Modifier = Modifier,
) {
    var vaultBusy by remember { mutableStateOf(false) }
    fun matrixOf(p: QrProfile) = QrShare.encodeOrNull(p.payloadText, QrStyleJson.decode(p.styleJson))
    fun extrasOf(s: QrStyle, kind: org.qraft.app.editor.PayloadKind) =
        RasterExtrasFactory.of(s, kind, 1024)
    val saveVault = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        if (uri == null) {
            vaultBusy = false
            GalleryVault.takePassphrase()
            return@rememberLauncherForActivityResult
        }
        val secret = GalleryVault.takePassphrase()
        scope.launch {
            try {
                notice(GalleryVault.save(context, repo, uri, secret))
            } finally {
                vaultBusy = false
            }
        }
    }
    val openVault = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) {
            vaultBusy = false
            GalleryVault.takePassphrase()
            return@rememberLauncherForActivityResult
        }
        val secret = GalleryVault.takePassphrase()
        scope.launch {
            try {
                val res = GalleryVault.open(context, repo, uri, secret)
                notice(res)
                if (res == R.string.profiles_restored) refresh()
            } finally {
                vaultBusy = false
            }
        }
    }
    ProfilesScreen(
        profiles = profiles,
        chrome = chrome,
        vaultBusy = vaultBusy,
        onDelete = { id ->
            scope.launch {
                repo.delete(id)
                GalleryStore.delete(context, id)
                refresh()
            }
        },
        onBackup = { secret ->
            if (vaultBusy) return@ProfilesScreen
            vaultBusy = true
            scope.launch {
                try {
                    clip(withContext(Dispatchers.Default) { BackupCrypto.wrap(repo.exportJson(), secret) })
                } finally {
                    vaultBusy = false
                }
            }
        },
        onRestore = { secret ->
            if (vaultBusy) return@ProfilesScreen
            vaultBusy = true
            scope.launch {
                try {
                    val res = GalleryVault.restoreClipboard(context, repo, secret)
                    notice(res)
                    if (res == R.string.profiles_restored) refresh()
                } finally {
                    vaultBusy = false
                }
            }
        },
        onSaveVault = { secret ->
            if (vaultBusy) return@ProfilesScreen
            if (secret.isBlank()) {
                notice(R.string.profiles_vault_need_passphrase)
                return@ProfilesScreen
            }
            GalleryVault.holdPassphrase(secret)
            vaultBusy = true
            saveVault.launch("qraft-vault.qraft")
        },
        onOpenVault = { secret ->
            if (vaultBusy) return@ProfilesScreen
            if (secret.isBlank()) {
                notice(R.string.profiles_vault_need_passphrase)
                return@ProfilesScreen
            }
            GalleryVault.holdPassphrase(secret)
            vaultBusy = true
            openVault.launch(arrayOf("*/*"))
        },
        onUndo = {
            val (snap, next) = ProfileHistory.undo(history)
            onHistory(next)
            if (snap != null) {
                val draft = ProfileApply.fromProfile(
                    QrProfile("undo", "Undo", snap.payloadText, styleJson = snap.styleJson),
                ).first
                onDraftStyle(draft, snap.styleJson, "Undo")
                onOpenHome()
            }
        },
        onExportJson = { p ->
            clip(
                QrExportJson.encode(
                    QrExportDocument(payloadText = p.payloadText, style = QrStyleJson.decode(p.styleJson)),
                ),
            )
        },
        onExportPng = { p ->
            val s = QrStyleJson.decode(p.styleJson)
            val kind = ProfileApply.fromProfile(p).first.kind
            matrixOf(p)?.let { QrShare.sharePng(context, it, s, extrasOf(s, kind), pngSize.px, p.payloadText) }
        },
        onExportSvg = { p ->
            matrixOf(p)?.let { clip(QrShare.svgText(it, QrStyleJson.decode(p.styleJson), p.payloadText)) }
        },
        onExportPdf = { p ->
            matrixOf(p)?.let { QrShare.sharePdf(context, it, QrStyleJson.decode(p.styleJson), p.payloadText) }
        },
        onAddWidget = { p ->
            if (!WidgetPin.request(context, p.id)) {
                notice(R.string.widget_pin_refused)
            }
        },
        onEditOnHome = { profile ->
            val applied = ProfileApply.fromProfile(profile)
            onDraftStyle(applied.first, QrStyleJson.encode(applied.second), profile.name)
            onOpenHome()
        },
        onCopyPayload = { p -> clip(p.payloadText) },
        onPrint = { p ->
            matrixOf(p)?.let {
                QrPrint.printSvg(context, p.name, QrShare.svgText(it, QrStyleJson.decode(p.styleJson), p.payloadText))
            }
        },
        onZip = { p ->
            val side = GalleryStore.readThumb(context, p.id)
            QrZipExport.share(context, QrZipExport.zipProfile(context, p, side))
        },
        onBatchPdf = { BatchPdfExport.share(context, profiles) },
        onOpenHome = onOpenHome,
        modifier = modifier,
    )
}
