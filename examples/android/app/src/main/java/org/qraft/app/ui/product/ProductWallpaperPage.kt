package org.qraft.app.ui.product

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.ui.restorePreviousWallpaper
import org.qraft.app.ui.setWallpaperFromDraft
import org.qraft.app.ui.wall.WallpaperEmpty
import org.qraft.app.ui.wall.WallpaperScreen
import org.qraft.render.QrStyle
import org.qraft.wallpaper.WallpaperTarget

@Composable
internal fun ProductWallpaperPage(
    draft: EditorDraft,
    style: QrStyle,
    margin: Float,
    onMarginChange: (Float) -> Unit,
    size: Pair<Int, Int>,
    onSavePng: () -> Unit,
    context: Context,
    scope: CoroutineScope,
    notice: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    fun apply(target: WallpaperTarget, pair: Boolean = false) {
        if (!WallpaperEmpty.canSet(draft)) return
        scope.launch {
            runCatching {
                setWallpaperFromDraft(context, draft, style, size, margin, target, pairDarkLight = pair)
            }.onFailure { notice(R.string.wallpaper_apply_failed) }
        }
    }
    WallpaperScreen(
        draft = draft,
        style = style,
        marginFraction = margin,
        onMarginChange = onMarginChange,
        widthPx = size.first,
        heightPx = size.second,
        onSetHome = { apply(WallpaperTarget.HOME) },
        onSetLock = { apply(WallpaperTarget.LOCK) },
        onSetBoth = { apply(WallpaperTarget.BOTH) },
        onSavePng = onSavePng,
        onRestore = {
            scope.launch {
                runCatching { restorePreviousWallpaper(context, WallpaperTarget.BOTH) }
                    .onFailure { notice(R.string.wallpaper_apply_failed) }
            }
        },
        onPair = { apply(WallpaperTarget.BOTH, pair = true) },
        modifier = modifier,
    )
}
