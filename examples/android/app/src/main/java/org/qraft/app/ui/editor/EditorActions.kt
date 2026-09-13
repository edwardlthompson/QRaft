package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.share.ExportPngSize
import org.qraft.app.ui.theme.SpacingMd

private enum class ExportPick {
    Json, Png, Svg, Pdf, StyleQr, AddWidget, ImportJson, CopyPayload, Print, Zip, Wallpaper
}

@Composable
fun EditorActions(
    canSave: Boolean,
    onSave: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onStyleQr: () -> Unit,
    onAddWidget: () -> Unit,
    onImportJson: () -> Unit = {},
    onCopyPayload: () -> Unit = {},
    onPrint: () -> Unit = {},
    onZip: () -> Unit = {},
    onSetWallpaper: () -> Unit = {},
    onPngSize: (ExportPngSize) -> Unit = {},
    pngSize: ExportPngSize = ExportPngSize.DEFAULT,
    onCurrentWifi: () -> Unit,
    onPasteClipboard: () -> Unit = {},
    onDecoratePhoto: () -> Unit = {},
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onNew: () -> Unit = {},
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    showSave: Boolean = true,
    showWifi: Boolean = true,
    showGalleryExtras: Boolean = false,
) {
    val labels = mapOf(
        ExportPick.Json to stringResource(R.string.export_json),
        ExportPick.Png to stringResource(R.string.export_png),
        ExportPick.Svg to stringResource(R.string.export_svg),
        ExportPick.Pdf to stringResource(R.string.export_pdf),
        ExportPick.StyleQr to stringResource(R.string.export_style_qr),
        ExportPick.AddWidget to stringResource(R.string.export_add_widget),
        ExportPick.ImportJson to stringResource(R.string.import_json),
        ExportPick.CopyPayload to stringResource(R.string.export_copy_payload),
        ExportPick.Print to stringResource(R.string.export_print),
        ExportPick.Zip to stringResource(R.string.export_zip),
        ExportPick.Wallpaper to stringResource(R.string.profiles_set_wallpaper),
    )
    val picks = if (showGalleryExtras) {
        ExportPick.entries.toList()
    } else {
        ExportPick.entries.filter {
            it != ExportPick.CopyPayload && it != ExportPick.Print &&
                it != ExportPick.Zip && it != ExportPick.Wallpaper
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        if (showSave) {
            Button(onClick = onSave, enabled = canSave) { Text(stringResource(R.string.editor_save_gallery)) }
            Button(onClick = onDuplicate, enabled = canSave) { Text(stringResource(R.string.editor_duplicate)) }
            Button(onClick = onNew) { Text(stringResource(R.string.editor_new)) }
            Button(onClick = onUndo, enabled = canUndo) { Text(stringResource(R.string.editor_undo)) }
            Button(onClick = onRedo, enabled = canRedo) { Text(stringResource(R.string.editor_redo)) }
        }
        MenuField(
            label = stringResource(R.string.editor_export),
            value = ExportPick.Png,
            options = picks,
            labelOf = { labels.getValue(it) },
            onSelect = { pick ->
                when (pick) {
                    ExportPick.Json -> onExportJson()
                    ExportPick.Png -> onExportPng()
                    ExportPick.Svg -> onExportSvg()
                    ExportPick.Pdf -> onExportPdf()
                    ExportPick.StyleQr -> onStyleQr()
                    ExportPick.AddWidget -> onAddWidget()
                    ExportPick.ImportJson -> onImportJson()
                    ExportPick.CopyPayload -> onCopyPayload()
                    ExportPick.Print -> onPrint()
                    ExportPick.Zip -> onZip()
                    ExportPick.Wallpaper -> onSetWallpaper()
                }
            },
        )
        MenuField(
            label = stringResource(R.string.export_png_size),
            value = pngSize,
            options = ExportPngSize.entries.toList(),
            labelOf = { "${it.px}px" },
            onSelect = onPngSize,
        )
        if (showWifi) {
            Button(onClick = onCurrentWifi) { Text(stringResource(R.string.editor_current_wifi)) }
        }
        Button(onClick = onPasteClipboard) { Text(stringResource(R.string.editor_paste_clipboard)) }
        Button(onClick = onDecoratePhoto) { Text(stringResource(R.string.editor_decorate_photo)) }
    }
}
