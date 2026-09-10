package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class QrRasterExtrasTest {
    @Test
    fun imageBackgroundIsDimmedInQuietZone() {
        val matrix = QrEncoder.encodeText("bg")
        val size = 128
        val raw = 0xFF0000FF.toInt()
        val extras = RasterExtras(bgPixels = IntArray(size * size) { raw })
        val result = StyledQrRasterizer.rasterize(matrix, size, QrStyle.DEFAULT, extras)
        assertNotEquals(raw, result.pixels[0])
        assertEquals(result.width, result.height)
    }

    @Test
    fun missingInlaySkipsBlit() {
        val matrix = QrEncoder.encodeText("inlay")
        val style = QrStyle(logoCutout = LogoCutout(enabled = true))
        val result = StyledQrRasterizer.rasterize(matrix, 96, style, RasterExtras())
        assertEquals(96, result.width)
        assertEquals(96, result.height)
    }
}
