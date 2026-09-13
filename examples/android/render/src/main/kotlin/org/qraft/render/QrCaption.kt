package org.qraft.render

import org.qraft.coreqr.SquareRasterizer

object QrCaption {
    /**
     * Extra pixels under the square QR for caption text.
     * Quiet zone is already inside the QR bitmap — only a snug text band is added.
     */
    fun bandHeightPx(squareWidth: Int): Int {
        val textSize = (squareWidth / 14f).coerceAtLeast(12f)
        val padTop = (textSize * 0.15f).toInt().coerceAtLeast(2)
        val padBottom = (textSize * 0.2f).toInt().coerceAtLeast(2)
        val textBlock = (textSize * 1.15f).toInt().coerceAtLeast(14)
        return padTop + textBlock + padBottom
    }

    fun extraHeight(square: SquareRasterizer.Result, style: QrStyle): Int {
        if (style.caption.text.isBlank()) return 0
        return bandHeightPx(square.width)
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
