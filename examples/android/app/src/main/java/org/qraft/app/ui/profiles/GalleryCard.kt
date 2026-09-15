package org.qraft.app.ui.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.ProfileApply
import org.qraft.app.ui.editor.ExportOverflow
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.data.QrProfile

@Composable
internal fun GalleryCard(
    profile: QrProfile,
    onEditOnHome: () -> Unit,
    onDelete: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onAddWidget: () -> Unit,
    onCopyPayload: () -> Unit,
    onPrint: () -> Unit,
    onZip: () -> Unit,
    onClose: () -> Unit,
) {
    val seed = ProfileApply.fromProfile(profile)
    val style = seed.second
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        EditorPreview(draft = seed.first, style = style)
        Text(text = profile.name, style = MaterialTheme.typography.titleMedium)
        Text(
            text = profile.tags.joinToString().ifBlank { profile.payloadText.take(80) },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onEditOnHome) { Text(stringResource(R.string.profiles_edit_home)) }
        ExportOverflow(
            showGalleryExtras = true,
            onExportJson = onExportJson,
            onExportPng = onExportPng,
            onExportSvg = onExportSvg,
            onExportPdf = onExportPdf,
            onStyleQr = {},
            onAddWidget = onAddWidget,
            onImportJson = {},
            onCopyPayload = onCopyPayload,
            onPrint = onPrint,
            onZip = onZip,
            onSetWallpaper = {},
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = onDelete) { Text(stringResource(R.string.profiles_delete)) }
            TextButton(onClick = onClose) { Text(stringResource(R.string.about_close)) }
        }
    }
}
