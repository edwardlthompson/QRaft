package org.qraft.render

import org.qraft.coreqr.QrMatrix

internal object BlobModules {
    fun paint(
        pixels: IntArray,
        stride: Int,
        originX: Int,
        originY: Int,
        quiet: Int,
        modulePx: Int,
        matrix: QrMatrix,
        style: QrStyle,
    ) {
        val radius = modulePx * 0.42
        val pad = (modulePx * 0.16).toInt().coerceAtLeast(1)
        val span = (modulePx * 0.68).toInt().coerceAtLeast(1)
        val round = span / 2
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (!darkData(matrix, x, y)) continue
                val fg = GradientPaint.moduleColor(style, x, matrix.size)
                val left = originX + (x + quiet) * modulePx
                val top = originY + (y + quiet) * modulePx
                RasterDraw.fillCircle(
                    pixels, stride, left + modulePx / 2.0, top + modulePx / 2.0, radius, fg,
                )
                if (darkData(matrix, x + 1, y)) {
                    RasterDraw.fillRoundedRect(
                        pixels, stride, left + modulePx / 2, top + pad, modulePx, span, round, fg,
                    )
                }
                if (darkData(matrix, x, y + 1)) {
                    RasterDraw.fillRoundedRect(
                        pixels, stride, left + pad, top + modulePx / 2, span, modulePx, round, fg,
                    )
                }
            }
        }
    }

    fun darkData(matrix: QrMatrix, x: Int, y: Int): Boolean {
        if (x !in 0 until matrix.size || y !in 0 until matrix.size) return false
        if (StyledQrPaint.isFinderCell(x, y, matrix.size)) return false
        return matrix.isDark(x, y)
    }
}
