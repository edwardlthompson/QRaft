package org.qraft.render

import org.qraft.coreqr.SquareRasterizer

object QrCaption {
    fun extraHeight(square: SquareRasterizer.Result, style: QrStyle): Int {
        if (style.caption.text.isBlank()) return 0
        val quietPx = (style.quietZoneModules.coerceAtLeast(0) * square.modulePx).coerceAtLeast(4)
        val band = (square.width / 8).coerceAtLeast(24)
        return quietPx + band
    }

    fun compose(square: SquareRasterizer.Result, style: QrStyle): SquareRasterizer.Result {
        val extra = extraHeight(square, style)
        if (extra == 0) return square
        val height = square.height + extra
        val pixels = IntArray(square.width * height) { style.backgroundArgb }
        for (y in 0 until square.height) {
            val src = y * square.width
            square.pixels.copyInto(pixels, y * square.width, src, src + square.width)
        }
        return square.copy(height = height, pixels = pixels)
    }
}
