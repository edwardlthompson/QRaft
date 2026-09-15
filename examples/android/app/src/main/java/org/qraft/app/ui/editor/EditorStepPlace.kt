package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.share.ExportPngSize
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.app.ui.wall.WallpaperTargetRow

@Composable
internal fun EditorStepPlace(
    saveName: String,
    onSaveNameChange: (String) -> Unit,
    canSetWallpaper: Boolean,
    pngSize: ExportPngSize,
    onPngSize: (ExportPngSize) -> Unit,
    onWallpaperHome: () -> Unit,
    onWallpaperLock: () -> Unit,
    onWallpaperBoth: () -> Unit,
    onWallpaperRestore: () -> Unit,
    onSharePng: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Text(
            text = stringResource(R.string.editor_step_save),
            style = MaterialTheme.typography.titleLarge,
        )
        OutlinedTextField(
            value = saveName,
            onValueChange = onSaveNameChange,
            label = { Text(stringResource(R.string.editor_save_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        WallpaperTargetRow(
            onHome = onWallpaperHome,
            onLock = onWallpaperLock,
            onBoth = onWallpaperBoth,
            onSavePng = onSharePng,
            onRestore = onWallpaperRestore,
            enabled = canSetWallpaper,
        )
        MenuField(
            label = stringResource(R.string.export_png_size),
            value = pngSize,
            options = ExportPngSize.entries.toList(),
            labelOf = { "${it.px}px" },
            onSelect = onPngSize,
        )
    }
}
