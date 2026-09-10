package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class FinderShapeTest {
    @Test
    fun hexFinderLeavesSquareCornerEmpty() {
        val matrix = QrEncoder.encodeText("hex-finder")
        val hex = StyledQrRasterizer.rasterize(
            matrix, 256, QrStyle(finderShape = FinderShape.HEX, finderPupil = FinderShape.HEX),
        )
        val square = StyledQrRasterizer.rasterize(matrix, 256, QrStyle.DEFAULT)
        val quiet = QrStyle.DEFAULT.quietZoneModules
        val x = hex.contentOriginX + quiet * hex.modulePx
        val y = hex.contentOriginY + quiet * hex.modulePx
        assertEquals(0xFFFFFFFF.toInt(), hex.pixels[y * hex.width + x])
        assertEquals(0xFF000000.toInt(), square.pixels[y * square.width + x])
    }

    @Test
    fun ringPupilLeavesFinderCenterOpen() {
        val matrix = QrEncoder.encodeText("ring-finder")
        val style = QrStyle(finderShape = FinderShape.RING, finderPupil = FinderShape.RING)
        val result = StyledQrRasterizer.rasterize(matrix, 256, style)
        val quiet = style.quietZoneModules
        val cx = result.contentOriginX + (3 + quiet) * result.modulePx + result.modulePx / 2
        val cy = result.contentOriginY + (3 + quiet) * result.modulePx + result.modulePx / 2
        assertNotEquals(style.foregroundArgb, result.pixels[cy * result.width + cx])
    }
}
