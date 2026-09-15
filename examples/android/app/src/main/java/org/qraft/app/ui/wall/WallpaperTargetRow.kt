package org.qraft.app.ui.wall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.SpacingMd

@Composable
fun WallpaperTargetRow(
    onHome: () -> Unit,
    onLock: () -> Unit,
    onBoth: () -> Unit,
    onSavePng: () -> Unit,
    onRestore: () -> Unit,
    onPair: (() -> Unit)? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        FilledTonalButton(onClick = onHome, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.wallpaper_set_home))
        }
        FilledTonalButton(onClick = onLock, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.wallpaper_set_lock))
        }
        FilledTonalButton(onClick = onBoth, enabled = enabled, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.wallpaper_set_both))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(
                onClick = onSavePng,
                enabled = enabled,
                modifier = Modifier.heightIn(min = MinTouchDp),
            ) { Text(stringResource(R.string.wallpaper_save_png)) }
            TextButton(
                onClick = onRestore,
                modifier = Modifier.heightIn(min = MinTouchDp),
            ) { Text(stringResource(R.string.wallpaper_restore)) }
            if (onPair != null) {
                TextButton(
                    onClick = onPair,
                    enabled = enabled,
                    modifier = Modifier.heightIn(min = MinTouchDp),
                ) { Text(stringResource(R.string.wallpaper_pair)) }
            }
        }
    }
}
