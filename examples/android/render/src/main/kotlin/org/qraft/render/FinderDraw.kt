package org.qraft.render

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

internal object FinderDraw {
    fun fillHexagon(
        pixels: IntArray,
        stride: Int,
        cx: Double,
        cy: Double,
        radius: Double,
        color: Int,
    ) {
        val x0 = maxOf(0, (cx - radius).toInt())
        val y0 = maxOf(0, (cy - radius).toInt())
        val x1 = min(stride, (cx + radius).toInt() + 1)
        val y1 = min(stride, (cy + radius).toInt() + 1)
        for (y in y0 until y1) {
            val row = y * stride
            val dy = abs(y + 0.5 - cy)
            for (x in x0 until x1) {
                if (insideHex(abs(x + 0.5 - cx), dy, radius)) pixels[row + x] = color
            }
        }
    }

    fun fillRing(
        pixels: IntArray,
        stride: Int,
        cx: Double,
        cy: Double,
        outer: Double,
        inner: Double,
        color: Int,
    ) {
        val outer2 = outer * outer
        val inner2 = inner.coerceAtLeast(0.0) * inner.coerceAtLeast(0.0)
        val x0 = maxOf(0, (cx - outer).toInt())
        val y0 = maxOf(0, (cy - outer).toInt())
        val x1 = min(stride, (cx + outer).toInt() + 1)
        val y1 = min(stride, (cy + outer).toInt() + 1)
        for (y in y0 until y1) {
            val row = y * stride
            val dy = y + 0.5 - cy
            for (x in x0 until x1) {
                val dx = x + 0.5 - cx
                val d2 = dx * dx + dy * dy
                if (d2 <= outer2 && d2 >= inner2) pixels[row + x] = color
            }
        }
    }

    private fun insideHex(dx: Double, dy: Double, radius: Double): Boolean {
        val halfH = radius * SQRT3_2
        return dx <= radius && dy <= halfH && (halfH - dy) >= dx * 0.5
    }

    private val SQRT3_2 = sqrt(3.0) / 2.0
}
