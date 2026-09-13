package org.qraft.widget

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * QR stays square (module grid); the widget frame may be any aspect ratio.
 * Bitmaps stay binder-safe — never URI/file providers (those break launcher load).
 */
object WidgetQrCanvas {
    /** Safe for ImageProvider(Bitmap) under the RemoteViews binder limit. */
    const val MAX_BITMAP_PX = 256
    const val MIN_PX = 64
    const val CHROME_RESERVE_DP = 28f

    fun showChrome(widthDp: Float, heightDp: Float): Boolean =
        min(widthDp, heightDp) >= 80f

    /** Largest square that fits in the widget box (optional chrome strip). */
    fun usableSideDp(widthDp: Float, heightDp: Float, chrome: Boolean): Float {
        val h = if (chrome) heightDp - CHROME_RESERVE_DP else heightDp
        return min(widthDp, h).coerceAtLeast(24f)
    }

    fun targetPx(density: Float, sideDp: Float): Int =
        (sideDp * density).roundToInt().coerceIn(MIN_PX, MAX_BITMAP_PX)

    fun snapToModules(px: Int, modulesWithQuiet: Int): Int {
        val m = modulesWithQuiet.coerceAtLeast(1)
        val modulePx = max(1, px / m)
        return (modulePx * m).coerceIn(MIN_PX, MAX_BITMAP_PX)
    }
}
