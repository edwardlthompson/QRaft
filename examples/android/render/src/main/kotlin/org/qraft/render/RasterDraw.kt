package org.qraft.render

import kotlin.math.min

internal object RasterDraw {
    fun fillRect(pixels: IntArray, stride: Int, left: Int, top: Int, w: Int, h: Int, color: Int) {
        val x0 = maxOf(0, left)
        val y0 = maxOf(0, top)
        val x1 = min(stride, left + w)
        val y1 = min(stride, top + h)
        for (y in y0 until y1) {
            val row = y * stride
            for (x in x0 until x1) pixels[row + x] = color
        }
    }

    fun fillRoundedRect(
        pixels: IntArray,
        stride: Int,
        left: Int,
        top: Int,
        w: Int,
        h: Int,
        radius: Int,
        color: Int,
    ) {
        val r = min(radius, min(w, h) / 2).toDouble()
        val x0 = maxOf(0, left)
        val y0 = maxOf(0, top)
        val x1 = min(stride, left + w)
        val y1 = min(stride, top + h)
        for (y in y0 until y1) {
            val py = y - top + 0.5
            val row = y * stride
            for (x in x0 until x1) {
                if (insideRounded(x - left + 0.5, py, w.toDouble(), h.toDouble(), r)) {
                    pixels[row + x] = color
                }
            }
        }
    }

    fun fillCircle(
        pixels: IntArray,
        stride: Int,
        cx: Double,
        cy: Double,
        radius: Double,
        color: Int,
    ) {
        val r2 = radius * radius
        val x0 = maxOf(0, (cx - radius).toInt())
        val y0 = maxOf(0, (cy - radius).toInt())
        val x1 = min(stride, (cx + radius).toInt() + 1)
        val y1 = min(stride, (cy + radius).toInt() + 1)
        for (y in y0 until y1) {
            val row = y * stride
            val dy = y + 0.5 - cy
            for (x in x0 until x1) {
                val dx = x + 0.5 - cx
                if (dx * dx + dy * dy <= r2) pixels[row + x] = color
            }
        }
    }

    private fun insideRounded(px: Double, py: Double, w: Double, h: Double, r: Double): Boolean {
        if (px < 0 || py < 0 || px > w || py > h) return false
        val cx = when {
            px < r -> r
            px > w - r -> w - r
            else -> return true
        }
        val cy = when {
            py < r -> r
            py > h - r -> h - r
            else -> return true
        }
        val dx = px - cx
        val dy = py - cy
        return dx * dx + dy * dy <= r * r
    }

    fun fillDiamond(pixels: IntArray, stride: Int, left: Int, top: Int, w: Int, h: Int, color: Int) {
        val cx = left + w / 2.0
        val cy = top + h / 2.0
        val rx = w / 2.0
        val ry = h / 2.0
        val x0 = maxOf(0, left)
        val y0 = maxOf(0, top)
        val x1 = min(stride, left + w)
        val y1 = min(stride, top + h)
        for (y in y0 until y1) {
            val row = y * stride
            val dy = kotlin.math.abs(y + 0.5 - cy) / ry
            for (x in x0 until x1) {
                val dx = kotlin.math.abs(x + 0.5 - cx) / rx
                if (dx + dy <= 1.0) pixels[row + x] = color
            }
        }
    }
}
