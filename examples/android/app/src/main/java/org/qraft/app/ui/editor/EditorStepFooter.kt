package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.EditorStep
import org.qraft.app.editor.EditorSteps
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.SpacingMd

@Composable
internal fun EditorStepFooter(
    step: EditorStep,
    canSave: Boolean,
    onStep: (EditorStep) -> Unit,
    onSave: () -> Unit,
    onExportPng: () -> Unit,
    onExportJson: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onStyleQr: () -> Unit,
    onAddWidget: () -> Unit,
    onImportJson: () -> Unit,
    onPrint: () -> Unit,
    onZip: () -> Unit,
    onConfirmStyleQr: () -> Unit,
    onDuplicate: () -> Unit,
    onNew: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = { onStep(EditorSteps.previous(step)) },
            enabled = step != EditorStep.Content,
            modifier = Modifier.widthIn(min = MinTouchDp),
        ) { Text(stringResource(R.string.editor_step_previous)) }
        Text(
            text = stringResource(stepLabel(step)),
            modifier = Modifier.widthIn(min = MinTouchDp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (EditorSteps.shown(step) == EditorStep.Place) {
                ExportOverflow(
                    showGalleryExtras = false,
                    onExportJson = onExportJson,
                    onExportPng = onExportPng,
                    onExportSvg = onExportSvg,
                    onExportPdf = onExportPdf,
                    onStyleQr = onStyleQr,
                    onAddWidget = onAddWidget,
                    onImportJson = onImportJson,
                    onCopyPayload = {},
                    onPrint = onPrint,
                    onZip = onZip,
                    onSetWallpaper = {},
                    onConfirmStyleQr = onConfirmStyleQr,
                    onDuplicate = onDuplicate,
                    onNew = onNew,
                )
            }
            Button(
                onClick = {
                    when (EditorSteps.shown(step)) {
                        EditorStep.Place -> onSave()
                        else -> onStep(EditorSteps.next(step))
                    }
                },
                enabled = EditorSteps.shown(step) != EditorStep.Place || canSave,
                modifier = Modifier.widthIn(min = MinTouchDp),
            ) {
                Text(
                    stringResource(
                        if (EditorSteps.shown(step) == EditorStep.Place) {
                            R.string.editor_save_gallery
                        } else {
                            R.string.editor_step_next
                        },
                    ),
                )
            }
        }
    }
}

internal fun stepLabel(step: EditorStep): Int = when (EditorSteps.shown(step)) {
    EditorStep.Content -> R.string.editor_step_content
    EditorStep.Look -> R.string.editor_step_look
    else -> R.string.editor_step_save
}
