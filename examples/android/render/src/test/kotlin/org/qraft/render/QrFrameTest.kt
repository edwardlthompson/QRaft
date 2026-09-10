package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.coreqr.QrEncoder
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class QrFrameTest {
    @Test
    fun thinFramePaintsQuietZoneNotModules() {
        val matrix = QrEncoder.encodeText("frame-me")
        val style = QrStyle(frame = QrFrame.THIN)
        val framed = StyledQrRasterizer.rasterize(matrix, 256, style)
        val plain = StyledQrRasterizer.rasterize(matrix, 256, QrStyle.DEFAULT)
        val y = framed.contentOriginY + 3
        val x = framed.contentOriginX + framed.contentSizePx / 2
        assertEquals(style.foregroundArgb, framed.pixels[y * framed.width + x])
        assertEquals(style.backgroundArgb, plain.pixels[y * plain.width + x])
        assertEquals(QrFrame.THIN, QrStyleJson.decode(QrStyleJson.encode(style)).frame)
        assertTrue(framed.contentSizePx > 32)
    }
}
