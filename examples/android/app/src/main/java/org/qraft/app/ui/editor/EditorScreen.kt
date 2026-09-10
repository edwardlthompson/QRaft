package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.app.ui.insets.bottomInsetPadding
import org.qraft.app.ui.stylepanel.StyleControls
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.QrStyle

@Composable
fun EditorScreen(
    draft: EditorDraft,
    style: QrStyle,
    onDraftChange: (EditorDraft) -> Unit,
    onStyleChange: (QrStyle) -> Unit,
    saveName: String,
    onSaveNameChange: (String) -> Unit,
    canSave: Boolean,
    validation: String?,
    onSave: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onStyleQr: () -> Unit,
    onPickBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onAddWidget: () -> Unit,
    onImportJson: () -> Unit = {},
    onCurrentWifi: () -> Unit,
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onNew: () -> Unit = {},
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(SpacingMd).bottomInsetPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Surface(tonalElevation = SpacingMd, modifier = Modifier.fillMaxWidth()) {
            EditorPreview(draft = draft, style = style)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .highRefreshScroll()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            val kindLabels = PayloadKind.entries.associateWith { stringResource(kindLabel(it)) }
            MenuField(
                label = stringResource(R.string.editor_kind),
                value = draft.kind,
                options = PayloadKind.entries.toList(),
                labelOf = { kindLabels.getValue(it) },
                onSelect = { onDraftChange(draft.copy(kind = it, primary = EditorDraft.defaultPrimary(it))) },
            )
            PayloadFields(draft = draft, onDraftChange = onDraftChange)
            if (validation != null) {
                Text(text = validation, color = MaterialTheme.colorScheme.error)
            }
            OutlinedTextField(
                value = saveName,
                onValueChange = onSaveNameChange,
                label = { Text(stringResource(R.string.editor_save_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(text = stringResource(R.string.nav_style), style = MaterialTheme.typography.titleSmall)
            StyleControls(
                style = style,
                onStyleChange = onStyleChange,
                onPickBackground = onPickBackground,
                onClearBackground = onClearBackground,
            )
            EditorActions(
                canSave = canSave,
                onSave = onSave,
                onExportJson = onExportJson,
                onExportPng = onExportPng,
                onExportSvg = onExportSvg,
                onExportPdf = onExportPdf,
                onStyleQr = onStyleQr,
                onAddWidget = onAddWidget,
                onImportJson = onImportJson,
                onCurrentWifi = onCurrentWifi,
                onUndo = onUndo,
                onRedo = onRedo,
                onDuplicate = onDuplicate,
                onNew = onNew,
                canUndo = canUndo,
                canRedo = canRedo,
            )
        }
    }
}

private fun kindLabel(kind: PayloadKind): Int = when (kind) {
    PayloadKind.Url -> R.string.editor_kind_url
    PayloadKind.Text -> R.string.editor_kind_text
    PayloadKind.Wifi -> R.string.editor_kind_wifi
    PayloadKind.VCard -> R.string.editor_kind_vcard
    PayloadKind.Email -> R.string.editor_kind_email
    PayloadKind.Sms -> R.string.editor_kind_sms
    PayloadKind.Phone -> R.string.editor_kind_phone
    PayloadKind.Crypto -> R.string.editor_kind_crypto
}
