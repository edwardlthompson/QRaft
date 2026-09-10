package org.qraft.app.ui.wall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.editor.EditorDraft
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.insets.bottomInsetPadding
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.QrStyle
import org.qraft.wallpaper.WallpaperSafeZone
import kotlin.math.roundToInt

private enum class WallAction { Home, Lock, Both, SavePng }

@Composable
fun WallpaperScreen(
    draft: EditorDraft,
    style: QrStyle,
    marginFraction: Float,
    onMarginChange: (Float) -> Unit,
    widthPx: Int,
    heightPx: Int,
    onSetHome: () -> Unit,
    onSetLock: () -> Unit,
    onSetBoth: () -> Unit,
    onSavePng: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clamped = WallpaperSafeZone.clampUserMargin(marginFraction.toDouble()).toFloat()
    val pct = (clamped * 100).roundToInt()
    val demo = WallpaperSafeZone.qrContentRect(widthPx, heightPx, clamped.toDouble())
    val actionLabels = mapOf(
        WallAction.Home to stringResource(R.string.wallpaper_set_home),
        WallAction.Lock to stringResource(R.string.wallpaper_set_lock),
        WallAction.Both to stringResource(R.string.wallpaper_set_both),
        WallAction.SavePng to stringResource(R.string.wallpaper_save_png),
    )
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
            Text(
                text = stringResource(R.string.wallpaper_margin, pct),
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = clamped,
                onValueChange = onMarginChange,
                valueRange = 0f..WallpaperSafeZone.MAX_USER_MARGIN.toFloat(),
            )
            Text(
                text = stringResource(R.string.wallpaper_overlay, widthPx, heightPx),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.wallpaper_safe_size, demo.width, demo.height),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.wallpaper_scan_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MenuField(
                label = stringResource(R.string.wallpaper_apply),
                value = WallAction.Lock,
                options = WallAction.entries.toList(),
                labelOf = { actionLabels.getValue(it) },
                onSelect = { action ->
                    when (action) {
                        WallAction.Home -> onSetHome()
                        WallAction.Lock -> onSetLock()
                        WallAction.Both -> onSetBoth()
                        WallAction.SavePng -> onSavePng()
                    }
                },
            )
        }
    }
}
