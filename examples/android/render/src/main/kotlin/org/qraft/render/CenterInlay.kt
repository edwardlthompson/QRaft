package org.qraft.render

object CenterInlay {
    fun blit(
        dest: IntArray,
        stride: Int,
        cx: Double,
        cy: Double,
        radius: Double,
        src: IntArray,
        srcW: Int,
        srcH: Int,
    ) {
        if (srcW <= 0 || srcH <= 0 || src.size < srcW * srcH) return
        val r = radius
        val x0 = maxOf(0, (cx - r).toInt())
        val y0 = maxOf(0, (cy - r).toInt())
        val x1 = minOf(stride, (cx + r).toInt() + 1)
        val y1 = minOf(stride, (cy + r).toInt() + 1)
        val d = maxOf(1.0, r * 2)
        for (y in y0 until y1) {
            val row = y * stride
            val dy = y + 0.5 - cy
            for (x in x0 until x1) {
                val dx = x + 0.5 - cx
                if (dx * dx + dy * dy > r * r) continue
                val u = ((dx + r) / d).coerceIn(0.0, 1.0)
                val v = ((dy + r) / d).coerceIn(0.0, 1.0)
                val sx = (u * (srcW - 1)).toInt().coerceIn(0, srcW - 1)
                val sy = (v * (srcH - 1)).toInt().coerceIn(0, srcH - 1)
                val pixel = src[sy * srcW + sx]
                if ((pixel ushr 24) > 16) dest[row + x] = pixel
            }
        }
    }
}
