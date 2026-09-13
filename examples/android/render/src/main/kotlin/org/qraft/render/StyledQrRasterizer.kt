package org.qraft.render

import org.qraft.coreqr.QrMatrix
import org.qraft.coreqr.SquareRasterizer

data class RasterExtras(
    val bgPixels: IntArray? = null,
    val inlayPixels: IntArray? = null,
    val inlayWidth: Int = 0,
    val inlayHeight: Int = 0,
)

object StyledQrRasterizer {
    fun rasterize(
        matrix: QrMatrix,
        sizePx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        extras: RasterExtras = RasterExtras(),
    ): SquareRasterizer.Result {
        if (canUseSquare(style, extras)) {
            return SquareRasterizer.rasterize(
                matrix,
                sizePx,
                SquareRasterizer.Options(
                    foregroundArgb = style.foregroundArgb,
                    backgroundArgb = style.backgroundArgb,
                    quietZoneModules = style.quietZoneModules,
                ),
            )
        }
        require(sizePx > 0)
        val quiet = style.quietZoneModules.coerceAtLeast(0)
        val modulesWithQuiet = matrix.size + 2 * quiet
        val modulePx = maxOf(1, sizePx / modulesWithQuiet)
        val contentSizePx = modulePx * modulesWithQuiet
        val originX = (sizePx - contentSizePx) / 2
        val originY = (sizePx - contentSizePx) / 2
        val pixels = fillBackground(sizePx, style, extras.bgPixels)
        StyledQrPaint.paintDataModules(pixels, sizePx, originX, originY, quiet, modulePx, matrix, style)
        StyledQrPaint.paintFinders(pixels, sizePx, originX, originY, quiet, modulePx, matrix.size, style)
        QrFramePainter.paint(pixels, sizePx, originX, originY, contentSizePx, style)
        if (style.logoCutout.enabled ||
            style.centerMark != CenterMark.NONE ||
            style.logoImagePath.isNotBlank() ||
            extras.inlayPixels != null
        ) {
            val side = modulePx * matrix.size
            val cx = originX + quiet * modulePx + side / 2.0
            val cy = originY + quiet * modulePx + side / 2.0
            val radius = (side * style.logoCutout.clampedFraction) / 2.0
            RasterDraw.fillCircle(pixels, sizePx, cx, cy, radius, style.backgroundArgb)
            val inlay = extras.inlayPixels
            if (inlay != null && extras.inlayWidth > 0) {
                CenterInlay.blit(pixels, sizePx, cx, cy, radius, inlay, extras.inlayWidth, extras.inlayHeight)
            }
        }
        if (style.cornerBadge) {
            CornerOccupancy.paint(
                pixels, sizePx, originX, originY, quiet, modulePx, matrix, style.foregroundArgb,
            )
        }
        return SquareRasterizer.Result(
            width = sizePx,
            height = sizePx,
            pixels = pixels,
            modulePx = modulePx,
            contentOriginX = originX,
            contentOriginY = originY,
            contentSizePx = contentSizePx,
        )
    }

    private fun canUseSquare(style: QrStyle, extras: RasterExtras): Boolean =
        style.moduleShape == ModuleShape.SQUARE &&
            style.finderShape == FinderShape.SQUARE &&
            style.finderPupil == FinderShape.SQUARE &&
            style.eyeColorArgb == style.foregroundArgb &&
            !style.hasOverlay &&
            !style.gradient.enabled &&
            extras.bgPixels == null &&
            extras.inlayPixels == null &&
            style.frame == QrFrame.NONE

    private fun fillBackground(sizePx: Int, style: QrStyle, bg: IntArray?): IntArray {
        if (bg != null && bg.size == sizePx * sizePx) {
            return IntArray(sizePx * sizePx) { i -> dim(bg[i]) }
        }
        return IntArray(sizePx * sizePx) { style.backgroundArgb }
    }

    private fun dim(color: Int): Int {
        val a = color ushr 24
        val r = ((color shr 16) and 0xFF) / 2 + 96
        val g = ((color shr 8) and 0xFF) / 2 + 96
        val b = (color and 0xFF) / 2 + 96
        return (a shl 24) or (r.coerceIn(0, 255) shl 16) or (g.coerceIn(0, 255) shl 8) or b.coerceIn(0, 255)
    }
}
