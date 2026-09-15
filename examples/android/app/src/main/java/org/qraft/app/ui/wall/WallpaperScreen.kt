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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.editor.EditorDraft
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.QrStyle
import org.qraft.wallpaper.WallpaperSafeZone
import kotlin.math.roundToInt

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
    onRestore: () -> Unit = {},
    onPair: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val clamped = WallpaperSafeZone.clampUserMargin(marginFraction.toDouble()).toFloat()
    val pct = (clamped * 100).roundToInt()
    var previewLock by rememberSaveable { mutableStateOf(true) }
    val lockLabel = stringResource(R.string.wallpaper_set_lock)
    val homeLabel = stringResource(R.string.wallpaper_set_home)
    val canSet = WallpaperEmpty.canSet(draft)
    Column(
        modifier = modifier.fillMaxSize().padding(SpacingMd),
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
                text = stringResource(
                    if (previewLock) R.string.wallpaper_preview_lock else R.string.wallpaper_preview_home,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            MenuField(
                label = stringResource(R.string.wallpaper_overlay, widthPx, heightPx),
                value = previewLock,
                options = listOf(false, true),
                labelOf = { lock -> if (lock) lockLabel else homeLabel },
                onSelect = { previewLock = it },
            )
            if (!canSet) {
                Text(
                    text = stringResource(R.string.editor_payload_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Text(
                text = stringResource(R.string.wallpaper_scan_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WallpaperTargetRow(
                onHome = onSetHome,
                onLock = onSetLock,
                onBoth = onSetBoth,
                onSavePng = onSavePng,
                onRestore = onRestore,
                onPair = onPair,
                enabled = canSet,
            )
        }
    }
}
