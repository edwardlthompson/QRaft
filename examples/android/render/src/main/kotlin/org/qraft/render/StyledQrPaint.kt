package org.qraft.render

import org.qraft.coreqr.QrMatrix

internal object StyledQrPaint {
    fun isFinderCell(x: Int, y: Int, size: Int): Boolean {
        fun inBox(ox: Int, oy: Int) = x in ox until ox + 7 && y in oy until oy + 7
        return inBox(0, 0) || inBox(size - 7, 0) || inBox(0, size - 7)
    }

    fun paintDataModules(
        pixels: IntArray,
        stride: Int,
        originX: Int,
        originY: Int,
        quiet: Int,
        modulePx: Int,
        matrix: QrMatrix,
        style: QrStyle,
    ) {
        if (style.moduleShape == ModuleShape.BLOB) {
            BlobModules.paint(pixels, stride, originX, originY, quiet, modulePx, matrix, style)
            return
        }
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (!matrix.isDark(x, y) || isFinderCell(x, y, matrix.size)) continue
                val fg = GradientPaint.moduleColor(style, x, matrix.size)
                val left = originX + (x + quiet) * modulePx
                val top = originY + (y + quiet) * modulePx
                when (style.moduleShape) {
                    ModuleShape.SQUARE -> RasterDraw.fillRect(pixels, stride, left, top, modulePx, modulePx, fg)
                    ModuleShape.ROUNDED ->
                        RasterDraw.fillRoundedRect(pixels, stride, left, top, modulePx, modulePx, modulePx / 3, fg)
                    ModuleShape.DOT -> RasterDraw.fillCircle(
                        pixels, stride, left + modulePx / 2.0, top + modulePx / 2.0, modulePx * 0.42, fg,
                    )
                    ModuleShape.DIAMOND -> RasterDraw.fillDiamond(pixels, stride, left, top, modulePx, modulePx, fg)
                    ModuleShape.PILL_H, ModuleShape.PILL_V -> RasterDraw.fillRoundedRect(
                        pixels, stride, left, top, modulePx, modulePx, modulePx / 2, fg,
                    )
                    ModuleShape.BLOB -> Unit
                }
            }
        }
    }

    fun paintFinders(
        pixels: IntArray,
        stride: Int,
        originX: Int,
        originY: Int,
        quiet: Int,
        modulePx: Int,
        size: Int,
        style: QrStyle,
    ) {
        val fg = style.eyeColorArgb
        val bg = style.backgroundArgb
        val origins = listOf(0 to 0, (size - 7) to 0, 0 to (size - 7))
        for ((mx, my) in origins) {
            val left = originX + (mx + quiet) * modulePx
            val top = originY + (my + quiet) * modulePx
            paintFinder(pixels, stride, left, top, modulePx, 7 * modulePx, fg, bg, style.finderShape, style.finderPupil)
        }
    }

    private fun paintFinder(
        pixels: IntArray,
        stride: Int,
        left: Int,
        top: Int,
        modulePx: Int,
        outer: Int,
        fg: Int,
        bg: Int,
        frame: FinderShape,
        pupil: FinderShape,
    ) {
        fun ring(shape: FinderShape, inset: Int, size: Int, color: Int) {
            val l = left + inset
            val t = top + inset
            val cx = l + size / 2.0
            val cy = t + size / 2.0
            val radius = size / 2.0
            when (shape) {
                FinderShape.SQUARE -> RasterDraw.fillRect(pixels, stride, l, t, size, size, color)
                FinderShape.ROUNDED -> RasterDraw.fillRoundedRect(pixels, stride, l, t, size, size, modulePx, color)
                FinderShape.CIRCLE -> RasterDraw.fillCircle(pixels, stride, cx, cy, radius, color)
                FinderShape.HEX -> FinderDraw.fillHexagon(pixels, stride, cx, cy, radius, color)
                FinderShape.RING -> FinderDraw.fillRing(
                    pixels, stride, cx, cy, radius, (radius - modulePx).coerceAtLeast(radius * 0.35), color,
                )
            }
        }
        ring(frame, 0, outer, fg)
        ring(frame, modulePx, 5 * modulePx, bg)
        ring(pupil, 2 * modulePx, 3 * modulePx, fg)
    }
}
