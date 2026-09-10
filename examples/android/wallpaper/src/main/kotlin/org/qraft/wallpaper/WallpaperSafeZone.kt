package org.qraft.wallpaper

/**
 * Wallpaper zoom/crop compensation. Android launchers often pan/zoom wallpapers
 * by ~5–15%; we keep the QR inside an inward safe rectangle so finder patterns
 * stay scannable. Never stretch modules — leftover area is padding/border.
 */
object WallpaperSafeZone {
    const val DEFAULT_MARGIN_FRACTION = 0.10
    const val MAX_USER_MARGIN = 0.20

    data class RectPx(
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int,
    ) {
        val right: Int get() = left + width
        val bottom: Int get() = top + height
        val centerX: Int get() = left + width / 2
        val centerY: Int get() = top + height / 2
        /** Largest square that fits inside this rect, centered. */
        fun inscribedSquare(): RectPx {
            val side = minOf(width, height)
            return RectPx(
                left = centerX - side / 2,
                top = centerY - side / 2,
                width = side,
                height = side,
            )
        }
    }

    /**
     * @param marginFraction inward crop margin in 0..0.4 (default 10%).
     */
    fun safeRectangle(
        totalWidthPx: Int,
        totalHeightPx: Int,
        marginFraction: Double = DEFAULT_MARGIN_FRACTION,
    ): RectPx {
        require(totalWidthPx > 0 && totalHeightPx > 0)
        require(marginFraction in 0.0..0.4) { "marginFraction must be in 0..0.4" }
        val widthSafe = (totalWidthPx * (1.0 - marginFraction)).toInt().coerceAtLeast(1)
        val heightSafe = (totalHeightPx * (1.0 - marginFraction)).toInt().coerceAtLeast(1)
        val left = (totalWidthPx - widthSafe) / 2
        val top = (totalHeightPx - heightSafe) / 2
        return RectPx(left, top, widthSafe, heightSafe)
    }

    /** Square QR content rect inside the safe zone (aspect preserved). */
    fun qrContentRect(
        totalWidthPx: Int,
        totalHeightPx: Int,
        marginFraction: Double = DEFAULT_MARGIN_FRACTION,
    ): RectPx = safeRectangle(totalWidthPx, totalHeightPx, clampUserMargin(marginFraction)).inscribedSquare()

    fun clampUserMargin(fraction: Double): Double = fraction.coerceIn(0.0, MAX_USER_MARGIN)
}
