package org.qraft.render

import org.qraft.coreqr.QrMatrix

object CornerOccupancy {
    const val SIDE = 3

    fun cells(matrixSize: Int): List<Pair<Int, Int>> {
        val x0 = (matrixSize - 7 - SIDE).coerceAtLeast(7)
        val y0 = 7
        val x1 = (x0 + SIDE).coerceAtMost(matrixSize - 7)
        val y1 = (y0 + SIDE).coerceAtMost(matrixSize)
        return buildList {
            for (y in y0 until y1) {
                for (x in x0 until x1) add(x to y)
            }
        }
    }

    fun hitsFinder(matrixSize: Int): Boolean =
        cells(matrixSize).any { StyledQrPaint.isFinderCell(it.first, it.second, matrixSize) }

    fun paint(
        pixels: IntArray,
        stride: Int,
        originX: Int,
        originY: Int,
        quiet: Int,
        modulePx: Int,
        matrix: QrMatrix,
        color: Int,
    ) {
        for ((x, y) in cells(matrix.size)) {
            val left = originX + (x + quiet) * modulePx
            val top = originY + (y + quiet) * modulePx
            RasterDraw.fillRoundedRect(pixels, stride, left, top, modulePx, modulePx, modulePx / 3, color)
        }
    }
}
