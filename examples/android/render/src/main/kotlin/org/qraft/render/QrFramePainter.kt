package org.qraft.render

internal object QrFramePainter {
    fun paint(
        pixels: IntArray,
        stride: Int,
        originX: Int,
        originY: Int,
        contentSize: Int,
        style: QrStyle,
    ) {
        if (style.frame == QrFrame.NONE || contentSize <= 8) return
        val thick = when (style.frame) {
            QrFrame.NONE -> return
            QrFrame.THIN -> 2
            QrFrame.BOLD -> 6
            QrFrame.ROUNDED -> 3
        }
        val color = style.foregroundArgb
        val left = originX + 2
        val top = originY + 2
        val size = contentSize - 4
        if (size <= thick * 2) return
        if (style.frame == QrFrame.ROUNDED) {
            RasterDraw.fillRoundedRect(pixels, stride, left, top, size, size, 8, color)
            RasterDraw.fillRoundedRect(
                pixels, stride, left + thick, top + thick, size - thick * 2, size - thick * 2, 6, style.backgroundArgb,
            )
        } else {
            RasterDraw.fillRect(pixels, stride, left, top, size, thick, color)
            RasterDraw.fillRect(pixels, stride, left, top + size - thick, size, thick, color)
            RasterDraw.fillRect(pixels, stride, left, top, thick, size, color)
            RasterDraw.fillRect(pixels, stride, left + size - thick, top, thick, size, color)
        }
    }
}
