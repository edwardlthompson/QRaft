package org.qraft.app.ui.product

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.gallery.GallerySave
import org.qraft.app.gallery.GalleryStore
import org.qraft.app.share.BatchPdfExport
import org.qraft.app.share.ExportPngSize
import org.qraft.app.share.QrPrint
import org.qraft.app.share.QrShare
import org.qraft.app.share.QrZipExport
import org.qraft.app.share.WidgetPin
import org.qraft.app.ui.profiles.GalleryChromeState
import org.qraft.app.ui.profiles.ProfilesScreen
import org.qraft.app.ui.setWallpaperFromProfile
import org.qraft.data.BackupCrypto
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.ProfileHistory
import org.qraft.data.ProfileSnapshot
import org.qraft.data.ProfileTags
import org.qraft.data.QrProfile
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.wallpaper.WallpaperTarget

@Composable
internal fun ProductProfilesPage(
    profiles: List<QrProfile>,
    history: List<ProfileSnapshot>,
    pngSize: ExportPngSize,
    onPngSize: (ExportPngSize) -> Unit,
    onHistory: (List<ProfileSnapshot>) -> Unit,
    onDraftStyle: (EditorDraft, String, String) -> Unit,
    onOpenHome: () -> Unit,
    size: Pair<Int, Int>,
    margin: Float,
    context: Context,
    scope: CoroutineScope,
    repo: DataStoreProfileRepository,
    clip: (String) -> Unit,
    refresh: suspend () -> Unit,
    chrome: GalleryChromeState,
    modifier: Modifier = Modifier,
) {
    fun matrixOf(p: QrProfile) = QrShare.encodeOrNull(p.payloadText, QrStyleJson.decode(p.styleJson))
    fun extrasOf(s: QrStyle, kind: org.qraft.app.editor.PayloadKind) =
        RasterExtrasFactory.of(s, kind, 1024)
    ProfilesScreen(
        profiles = profiles,
        chrome = chrome,
        onDelete = { id ->
            scope.launch {
                repo.delete(id)
                GalleryStore.delete(context, id)
                refresh()
            }
        },
        onBackup = { secret -> scope.launch { clip(BackupCrypto.wrap(repo.exportJson(), secret)) } },
        onRestore = { secret ->
            val text = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
            val json = BackupCrypto.unwrap(text, secret)
            if (json != null) scope.launch { repo.importJson(json); refresh() }
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
            matrixOf(p)?.let { QrShare.sharePng(context, it, s, extrasOf(s, kind), pngSize.px) }
        },
        onExportSvg = { p -> matrixOf(p)?.let { clip(QrShare.svgText(it, QrStyleJson.decode(p.styleJson))) } },
        onExportPdf = { p -> matrixOf(p)?.let { QrShare.sharePdf(context, it, QrStyleJson.decode(p.styleJson)) } },
        onAddWidget = { p ->
            if (!WidgetPin.request(context, p.id)) {
                Toast.makeText(context, R.string.widget_pin_refused, Toast.LENGTH_LONG).show()
            }
        },
        onSaveEdit = { existing, name, tags, draft, style ->
            scope.launch {
                GallerySave.update(
                    context = context,
                    repo = repo,
                    existing = existing,
                    name = name,
                    draft = draft,
                    style = style,
                    now = System.currentTimeMillis(),
                    tags = ProfileTags.parse(tags),
                )
                refresh()
            }
        },
        onEditOnHome = { profile ->
            val applied = ProfileApply.fromProfile(profile)
            onDraftStyle(applied.first, QrStyleJson.encode(applied.second), profile.name)
            onOpenHome()
        },
        onSetWallpaper = { p ->
            scope.launch { setWallpaperFromProfile(context, p, size, margin, WallpaperTarget.LOCK) }
        },
        onCopyPayload = { p -> clip(p.payloadText) },
        onPrint = { p ->
            matrixOf(p)?.let { QrPrint.printSvg(context, p.name, QrShare.svgText(it, QrStyleJson.decode(p.styleJson))) }
        },
        onZip = { p ->
            val side = GalleryStore.readThumb(context, p.id)
            QrZipExport.share(context, QrZipExport.zipProfile(context, p, side))
        },
        onBatchPdf = { BatchPdfExport.share(context, profiles) },
        onPngSize = onPngSize,
        pngSize = pngSize,
        modifier = modifier,
    )
}
