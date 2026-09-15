package org.qraft.app.ui.product

import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.qraft.app.R
import org.qraft.app.editor.DraftHistory
import org.qraft.app.editor.DraftSnap
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.EditorStep
import org.qraft.app.editor.PayloadKind
import org.qraft.app.share.ExportPngSize
import org.qraft.app.share.QrPrint
import org.qraft.app.share.QrShare
import org.qraft.app.share.QrZipExport
import org.qraft.app.share.ShareIntake
import org.qraft.app.share.WidgetPin
import org.qraft.app.ui.editor.EditorScreen
import org.qraft.app.ui.restorePreviousWallpaper
import org.qraft.app.ui.setWallpaperFromDraft
import org.qraft.app.ui.wall.WallpaperEmpty
import org.qraft.app.wifi.CurrentWifi
import org.qraft.coreqr.QrMatrix
import org.qraft.data.QrProfile
import org.qraft.render.CenterMark
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.RasterExtras
import org.qraft.render.StyleJsonQr
import org.qraft.wallpaper.WallpaperTarget

@Composable
internal fun ProductHomeEditor(
    draft: EditorDraft,
    style: QrStyle,
    styleJson: String,
    saveName: String,
    canSave: Boolean,
    validation: String?,
    pngSize: ExportPngSize,
    past: List<DraftSnap>,
    future: List<DraftSnap>,
    onDraft: (EditorDraft) -> Unit,
    onStyleJson: (String) -> Unit,
    onSaveName: (String) -> Unit,
    onPastFuture: (List<DraftSnap>, List<DraftSnap>) -> Unit,
    onSave: () -> Unit,
    matrixOrNull: () -> QrMatrix?,
    extrasOf: () -> RasterExtras,
    clip: (String) -> Unit,
    pickBg: ManagedActivityResultLauncher<Array<String>, android.net.Uri?>,
    pickLogo: ManagedActivityResultLauncher<Array<String>, android.net.Uri?>,
    pickExport: ManagedActivityResultLauncher<Array<String>, android.net.Uri?>,
    pickPoster: ManagedActivityResultLauncher<Array<String>, android.net.Uri?>,
    wifiPermission: ManagedActivityResultLauncher<String, Boolean>,
    step: EditorStep,
    onStep: (EditorStep) -> Unit,
    onPngSize: (ExportPngSize) -> Unit,
    onNotice: (Int) -> Unit,
    wallSize: Pair<Int, Int>,
    wallMargin: Float,
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    fun applyWall(target: WallpaperTarget, pair: Boolean = false) {
        if (!WallpaperEmpty.canSet(draft)) return
        scope.launch {
            runCatching {
                setWallpaperFromDraft(context, draft, style, wallSize, wallMargin, target, pairDarkLight = pair)
            }.onFailure { onNotice(R.string.wallpaper_apply_failed) }
        }
    }
    EditorScreen(
        draft = draft,
        style = style,
        onDraftChange = onDraft,
        onStyleChange = { onStyleJson(QrStyleJson.encode(it)) },
        saveName = saveName,
        onSaveNameChange = onSaveName,
        canSave = canSave,
        validation = validation,
        onSave = onSave,
        onExportJson = {
            draft.toPayload()?.encodeText()?.let {
                clip(QrExportJson.encode(QrExportDocument(payloadText = it, style = style)))
                onNotice(R.string.export_copied)
            } ?: onNotice(R.string.editor_payload_empty)
        },
        onExportPng = {
            val matrix = matrixOrNull()
            if (matrix == null) {
                onNotice(R.string.editor_payload_empty)
            } else {
                QrShare.sharePng(
                    context, matrix, style, extrasOf(), pngSize.px,
                    payloadHint = draft.toPayload()?.encodeText().orEmpty(),
                )
            }
        },
        onExportSvg = {
            matrixOrNull()?.let {
                clip(QrShare.svgText(it, style, draft.toPayload()?.encodeText().orEmpty()))
                onNotice(R.string.export_copied)
            } ?: onNotice(R.string.editor_payload_empty)
        },
        onExportPdf = {
            matrixOrNull()?.let {
                QrShare.sharePdf(context, it, style, draft.toPayload()?.encodeText().orEmpty())
            }
        },
        onStyleQr = { onDraft(EditorDraft(primary = StyleJsonQr.payload(style))) },
        onPickBackground = { pickBg.launch(arrayOf("image/*")) },
        onClearBackground = { onStyleJson(QrStyleJson.encode(style.copy(imageBackgroundPath = ""))) },
        onPickLogo = { pickLogo.launch(arrayOf("image/*")) },
        onClearLogo = { onStyleJson(QrStyleJson.encode(style.copy(logoImagePath = ""))) },
        onAddWidget = {
            if (!WidgetPin.requestPick(context)) {
                onNotice(R.string.widget_pin_refused)
            }
        },
        onImportJson = { pickExport.launch(arrayOf("application/json", "text/*", "*/*")) },
        onPrint = {
            matrixOrNull()?.let {
                QrPrint.printSvg(
                    context,
                    saveName,
                    QrShare.svgText(it, style, draft.toPayload()?.encodeText().orEmpty()),
                )
            }
        },
        onZip = {
            val text = draft.toPayload()?.encodeText() ?: return@EditorScreen
            val profile = QrProfile(
                id = "draft",
                name = saveName,
                payloadText = text,
                styleJson = styleJson,
            )
            QrZipExport.share(context, QrZipExport.zipProfile(context, profile, null))
        },
        onCurrentWifi = {
            when {
                CurrentWifi.needsLocationPermission(context) -> {
                    onNotice(R.string.editor_wifi_need_location)
                    wifiPermission.launch(CurrentWifi.locationPermission)
                }
                else -> {
                    val ssid = CurrentWifi.ssidOrNull(context)
                    if (ssid == null) {
                        onNotice(R.string.editor_wifi_unavailable)
                    } else {
                        onDraft(draft.copy(kind = PayloadKind.Wifi, primary = ssid))
                    }
                }
            }
        },
        onPasteClipboard = { pasteClipboard(context, onDraft, onNotice) },
        onDecoratePhoto = { pickPoster.launch(arrayOf("image/*")) },
        onUndo = {
            val (snap, p, f) = DraftHistory.undo(past, future)
            onPastFuture(p, f)
            snap?.let { onDraft(it.draft); onStyleJson(it.styleJson); onSaveName(it.name) }
        },
        onRedo = {
            val (snap, p, f) = DraftHistory.redo(past, future)
            onPastFuture(p, f)
            snap?.let { onDraft(it.draft); onStyleJson(it.styleJson); onSaveName(it.name) }
        },
        onDuplicate = {
            onSaveName(context.getString(R.string.editor_duplicate_suffix, saveName))
            onSave()
            onStep(EditorStep.Content)
        },
        onNew = {
            onDraft(EditorDraft())
            onStyleJson(QrStyleJson.encode(QrStyle.DEFAULT))
            onSaveName(context.getString(R.string.editor_new_name))
            onStep(EditorStep.Content)
        },
        canUndo = past.size > 1,
        canRedo = future.isNotEmpty(),
        step = step,
        onStep = onStep,
        pngSize = pngSize,
        onPngSize = onPngSize,
        onWallpaperHome = { applyWall(WallpaperTarget.HOME) },
        onWallpaperLock = { applyWall(WallpaperTarget.LOCK) },
        onWallpaperBoth = { applyWall(WallpaperTarget.BOTH) },
        onWallpaperRestore = {
            scope.launch {
                runCatching { restorePreviousWallpaper(context, WallpaperTarget.BOTH) }
                    .onFailure { onNotice(R.string.wallpaper_apply_failed) }
            }
        },
        modifier = modifier,
    )
}

internal fun logoStyleFromPath(style: QrStyle, path: String): QrStyle = style.copy(
    logoImagePath = path,
    logoCutout = style.logoCutout.copy(enabled = true),
    centerMark = if (style.centerMark == CenterMark.NONE) CenterMark.CUTOUT else style.centerMark,
)

private fun pasteClipboard(context: Context, onDraft: (EditorDraft) -> Unit, onNotice: (Int) -> Unit) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()?.trim().orEmpty()
    if (text.isEmpty()) {
        onNotice(R.string.editor_clipboard_empty)
    } else {
        onDraft(ShareIntake.parse(text) ?: EditorDraft(kind = PayloadKind.Text, primary = text))
        onNotice(R.string.editor_clipboard_applied)
    }
}
