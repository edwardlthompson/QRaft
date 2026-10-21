package org.qraft.app.ui.wall

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.ui.adaptive.AdaptiveTwoPane
import org.qraft.app.ui.adaptive.rememberQraftWidthClass
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.insets.bottomInsetPadding
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
    val demo = WallpaperSafeZone.qrContentRect(widthPx, heightPx, clamped.toDouble())
    val widthClass = rememberQraftWidthClass()
    AdaptiveTwoPane(
        widthClass = widthClass,
        modifier = modifier.fillMaxSize().padding(SpacingMd).bottomInsetPadding(),
        preview = {
            Surface(tonalElevation = SpacingMd, modifier = Modifier.fillMaxWidth()) {
                EditorPreview(draft = draft, style = style)
            }
        },
        content = {
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
            Text(
                text = stringResource(R.string.wallpaper_safe_size, demo.width, demo.height),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        },
    )
}
