package org.qraft.app.ui.product

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.qraft.app.R
import org.qraft.app.editor.DraftHistory
import org.qraft.app.editor.DraftSnap
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.app.share.ExportPngSize
import org.qraft.app.share.QrShare
import org.qraft.app.share.ShareIntake
import org.qraft.app.share.WidgetPin
import org.qraft.app.ui.editor.EditorScreen
import org.qraft.app.wifi.CurrentWifi
import org.qraft.coreqr.QrMatrix
import org.qraft.render.CenterMark
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.RasterExtras
import org.qraft.render.StyleJsonQr

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
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
            }
        },
        onExportPng = { matrixOrNull()?.let { QrShare.sharePng(context, it, style, extrasOf(), pngSize.px) } },
        onExportSvg = { matrixOrNull()?.let { clip(QrShare.svgText(it, style)) } },
        onExportPdf = { matrixOrNull()?.let { QrShare.sharePdf(context, it, style) } },
        onStyleQr = { onDraft(EditorDraft(primary = StyleJsonQr.payload(style))) },
        onPickBackground = { pickBg.launch(arrayOf("image/*")) },
        onClearBackground = { onStyleJson(QrStyleJson.encode(style.copy(imageBackgroundPath = ""))) },
        onPickLogo = { pickLogo.launch(arrayOf("image/*")) },
        onClearLogo = { onStyleJson(QrStyleJson.encode(style.copy(logoImagePath = ""))) },
        onAddWidget = {
            if (!WidgetPin.requestPick(context)) {
                Toast.makeText(context, R.string.widget_pin_refused, Toast.LENGTH_LONG).show()
            }
        },
        onImportJson = { pickExport.launch(arrayOf("application/json", "text/*", "*/*")) },
        onCurrentWifi = {
            when {
                CurrentWifi.needsLocationPermission(context) -> {
                    Toast.makeText(context, R.string.editor_wifi_need_location, Toast.LENGTH_SHORT).show()
                    wifiPermission.launch(CurrentWifi.locationPermission)
                }
                else -> {
                    val ssid = CurrentWifi.ssidOrNull(context)
                    if (ssid == null) {
                        Toast.makeText(context, R.string.editor_wifi_unavailable, Toast.LENGTH_SHORT).show()
                    } else {
                        onDraft(draft.copy(kind = PayloadKind.Wifi, primary = ssid))
                    }
                }
            }
        },
        onPasteClipboard = { pasteClipboard(context, onDraft) },
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
        onDuplicate = { onSaveName("$saveName copy"); onSave() },
        onNew = {
            onDraft(EditorDraft())
            onStyleJson(QrStyleJson.encode(QrStyle.DEFAULT))
            onSaveName("Website")
        },
        canUndo = past.size > 1,
        canRedo = future.isNotEmpty(),
        modifier = modifier,
    )
}

internal fun logoStyleFromPath(style: QrStyle, path: String): QrStyle = style.copy(
    logoImagePath = path,
    logoCutout = style.logoCutout.copy(enabled = true),
    centerMark = if (style.centerMark == CenterMark.NONE) CenterMark.CUTOUT else style.centerMark,
)

private fun pasteClipboard(context: Context, onDraft: (EditorDraft) -> Unit) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()?.trim().orEmpty()
    if (text.isEmpty()) {
        Toast.makeText(context, R.string.editor_clipboard_empty, Toast.LENGTH_SHORT).show()
    } else {
        onDraft(ShareIntake.parse(text) ?: EditorDraft(kind = PayloadKind.Text, primary = text))
        Toast.makeText(context, R.string.editor_clipboard_applied, Toast.LENGTH_SHORT).show()
    }
}
