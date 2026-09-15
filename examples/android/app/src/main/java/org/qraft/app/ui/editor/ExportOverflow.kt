package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.theme.MinTouchDp

@Composable
internal fun ExportOverflow(
    showGalleryExtras: Boolean,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onStyleQr: () -> Unit,
    onAddWidget: () -> Unit,
    onImportJson: () -> Unit,
    onCopyPayload: () -> Unit,
    onPrint: () -> Unit,
    onZip: () -> Unit,
    onSetWallpaper: () -> Unit,
    onConfirmStyleQr: (() -> Unit)? = null,
    onDuplicate: () -> Unit = {},
    onNew: () -> Unit = {},
    modifier: Modifier = Modifier,
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
        ExportPick.Duplicate to stringResource(R.string.editor_duplicate),
        ExportPick.New to stringResource(R.string.editor_new),
    )
    val groupLabels = mapOf(
        ExportGroup.Takeaway to stringResource(R.string.export_group_share),
        ExportGroup.Library to stringResource(R.string.export_group_library),
        ExportGroup.Draft to stringResource(R.string.export_group_draft),
    )
    val picks = if (showGalleryExtras) ExportMenu.gallery() else ExportMenu.home()
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.heightIn(min = MinTouchDp)) {
            Text(stringResource(R.string.export_more))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ExportMenu.grouped(picks).forEach { (group, items) ->
                DropdownMenuItem(
                    text = { Text(groupLabels.getValue(group)) },
                    onClick = {},
                    enabled = false,
                )
                items.forEach { pick ->
                    DropdownMenuItem(
                        text = { Text(labels.getValue(pick)) },
                        onClick = {
                            expanded = false
                            when (pick) {
                                ExportPick.Json -> onExportJson()
                                ExportPick.Png -> onExportPng()
                                ExportPick.Svg -> onExportSvg()
                                ExportPick.Pdf -> onExportPdf()
                                ExportPick.StyleQr -> (onConfirmStyleQr ?: onStyleQr)()
                                ExportPick.AddWidget -> onAddWidget()
                                ExportPick.ImportJson -> onImportJson()
                                ExportPick.CopyPayload -> onCopyPayload()
                                ExportPick.Print -> onPrint()
                                ExportPick.Zip -> onZip()
                                ExportPick.Wallpaper -> onSetWallpaper()
                                ExportPick.Duplicate -> onDuplicate()
                                ExportPick.New -> onNew()
                            }
                        },
                        modifier = Modifier.heightIn(min = MinTouchDp),
                    )
                }
            }
        }
    }
}
