package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class QuietZonePresetTest {
    @Test
    fun presetsFourSixEightScaleModules() {
        val matrix = QrEncoder.encodeText("quiet-presets")
        val four = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(quietZoneModules = 4))
        val six = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(quietZoneModules = 6))
        val eight = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(quietZoneModules = 8))
        assertTrue(four.modulePx >= six.modulePx)
        assertTrue(six.modulePx >= eight.modulePx)
        assertEquals(matrix.size + 8, four.contentSizePx / four.modulePx)
        assertEquals(matrix.size + 16, eight.contentSizePx / eight.modulePx)
    }
}
