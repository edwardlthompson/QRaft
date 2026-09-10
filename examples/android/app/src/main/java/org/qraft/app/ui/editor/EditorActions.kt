package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.theme.SpacingMd

private enum class ExportPick { Json, Png, Svg, Pdf, StyleQr, AddWidget, ImportJson }

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
    onCurrentWifi: () -> Unit,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onNew: () -> Unit = {},
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    showSave: Boolean = true,
    showWifi: Boolean = true,
) {
    val labels = mapOf(
        ExportPick.Json to stringResource(R.string.export_json),
        ExportPick.Png to stringResource(R.string.export_png),
        ExportPick.Svg to stringResource(R.string.export_svg),
        ExportPick.Pdf to stringResource(R.string.export_pdf),
        ExportPick.StyleQr to stringResource(R.string.export_style_qr),
        ExportPick.AddWidget to stringResource(R.string.export_add_widget),
        ExportPick.ImportJson to stringResource(R.string.import_json),
    )
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
            options = ExportPick.entries.toList(),
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
                }
            },
        )
        if (showWifi) {
            Button(onClick = onCurrentWifi) { Text(stringResource(R.string.editor_current_wifi)) }
        }
    }
}
