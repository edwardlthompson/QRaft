package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class QrCaptionTest {
    @Test
    fun captionMakesImageTallerNotWider() {
        val matrix = QrEncoder.encodeText("caption")
        val square = StyledQrRasterizer.rasterize(matrix, 128)
        val styled = QrStyle(caption = CaptionSpec("Hello"))
        val tall = QrCaption.compose(square, styled)
        assertEquals(square.width, tall.width)
        assertTrue(tall.height > square.height)
        val gapY = square.height
        assertEquals(styled.backgroundArgb, tall.pixels[gapY * tall.width + tall.width / 2])
    }

    @Test
    fun blankCaptionKeepsSquare() {
        val matrix = QrEncoder.encodeText("blank")
        val square = StyledQrRasterizer.rasterize(matrix, 64)
        val same = QrCaption.compose(square, QrStyle.DEFAULT)
        assertEquals(square.width, same.width)
        assertEquals(square.height, same.height)
    }

    @Test
    fun captionBandIsSnugNotDoubleQuietZone() {
        val matrix = QrEncoder.encodeText("snug")
        val square = StyledQrRasterizer.rasterize(matrix, 256)
        val styled = QrStyle(caption = CaptionSpec("Hi"), quietZoneModules = 4)
        val band = QrCaption.extraHeight(square, styled)
        // Old layout added quietZone*modulePx + width/8 (~96+32); snug band is text-sized only.
        assertTrue(band < square.width / 6)
        assertTrue(band > 0)
        val tall = QrCaption.compose(square, styled)
        assertTrue(tall.height - square.height < square.width / 6)
    }

    @Test
    fun overlayHasOverlayFlag() {
        assertTrue(QrStyle(centerMark = CenterMark.LINK).hasOverlay)
        assertFalse(QrStyle.DEFAULT.hasOverlay)
    }
}
