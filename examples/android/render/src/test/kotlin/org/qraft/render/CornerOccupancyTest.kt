package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class CornerOccupancyTest {
    @Test
    fun badgeCellsMissFinders() {
        val size = QrEncoder.encodeText("badge-occ").size
        assertFalse(CornerOccupancy.hitsFinder(size))
        assertTrue(CornerOccupancy.cells(size).isNotEmpty())
        assertTrue(QrStyle(cornerBadge = true).hasOverlay)
    }

    @Test
    fun badgePaintLeavesFinderCornerDark() {
        val matrix = QrEncoder.encodeText("badge-finder")
        val result = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(cornerBadge = true))
        val quiet = QrStyle.DEFAULT.quietZoneModules
        val fx = result.contentOriginX + (matrix.size - 1 + quiet) * result.modulePx + result.modulePx / 2
        val fy = result.contentOriginY + quiet * result.modulePx + result.modulePx / 2
        assertEquals(0xFF000000.toInt(), result.pixels[fy * result.width + fx])
    }
}
