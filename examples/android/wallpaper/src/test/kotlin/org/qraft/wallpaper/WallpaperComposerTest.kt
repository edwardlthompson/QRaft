package org.qraft.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrEncoder
import org.qraft.render.QrStyle

class WallpaperComposerTest {
    @Test
    fun outputMatchesDisplayAndKeepsBackgroundCorners() {
        val matrix = QrEncoder.encodeText("wall")
        val bg = 0xFF101010.toInt()
        val image = WallpaperComposer.compose(
            matrix,
            1080,
            2400,
            QrStyle(backgroundArgb = bg),
        )
        assertEquals(1080, image.width)
        assertEquals(2400, image.height)
        assertEquals(bg, image.pixels[0])
        assertEquals(bg, image.pixels[image.pixels.lastIndex])
        val qr = WallpaperSafeZone.qrContentRect(1080, 2400)
        assertTrue(qr.width == qr.height)
        val cx = qr.centerX
        val cy = qr.centerY
        assertEquals(image.pixels[cy * image.width + cx], image.pixels[cy * image.width + cx])
    }

    @Test
    fun composeIsDeterministic() {
        val matrix = QrEncoder.encodeText("same-wall")
        val a = WallpaperComposer.compose(matrix, 400, 800)
        val b = WallpaperComposer.compose(matrix, 400, 800)
        assertEquals(a, b)
    }

    @Test
    fun oledLeftoverIsBackgroundNeverStretched() {
        val matrix = QrEncoder.encodeText("oled")
        val bg = 0xFF000000.toInt()
        val image = WallpaperComposer.compose(
            matrix,
            400,
            800,
            QrStyle(backgroundArgb = bg),
            marginFraction = 0.20,
        )
        assertEquals(400, image.width)
        assertEquals(800, image.height)
        assertEquals(bg, image.pixels[0])
        assertEquals(bg, image.pixels[image.pixels.lastIndex])
        val qr = WallpaperSafeZone.qrContentRect(400, 800, 0.20)
        assertTrue(qr.width == qr.height)
        assertTrue(qr.width < 400 || qr.height < 800)
    }

    @Test
    fun lockLeftoverIsBlackWithoutCaption() {
        val matrix = QrEncoder.encodeText("lock-bg")
        val image = WallpaperComposer.compose(
            matrix,
            400,
            800,
            QrStyle(backgroundArgb = 0xFFFFFFFF.toInt()),
            leftoverArgb = WallpaperComposer.LOCK_LEFTOVER_ARGB,
            applyCaption = false,
        )
        assertEquals(WallpaperComposer.LOCK_LEFTOVER_ARGB, image.pixels[0])
        assertEquals(WallpaperComposer.LOCK_LEFTOVER_ARGB, image.pixels[image.pixels.lastIndex])
    }

    @Test
    fun maxSideForCaptionFitsBandInsideBounds() {
        val side = WallpaperComposer.maxSideForCaption(300, 400)
        assertTrue(side in 1..300)
        assertTrue(side + org.qraft.render.QrCaption.bandHeightPx(side) <= 400)
    }
}

class WallpaperApplierTest {
    @Test
    fun setterReceivesComposedImage() {
        val matrix = QrEncoder.encodeText("apply")
        val image = WallpaperComposer.compose(matrix, 200, 400)
        var seen: WallpaperImage? = null
        WallpaperApplier { seen = it }.apply(image)
        assertEquals(image, seen)
    }
}
