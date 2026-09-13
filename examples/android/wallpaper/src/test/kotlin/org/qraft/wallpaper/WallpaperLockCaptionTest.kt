package org.qraft.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.coreqr.QrEncoder
import org.qraft.render.CaptionSpec
import org.qraft.render.QrStyle
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class WallpaperLockCaptionTest {
    @Test
    fun composeLockIncludesCaptionAndBlackCorners() {
        val matrix = QrEncoder.encodeText("lock-caption")
        val style = QrStyle(
            backgroundArgb = 0xFFFFFFFF.toInt(),
            foregroundArgb = 0xFF000000.toInt(),
            caption = CaptionSpec("Scan me"),
        )
        val image = WallpaperComposer.composeLock(matrix, 400, 800, style)
        assertEquals(400, image.width)
        assertEquals(800, image.height)
        assertEquals(WallpaperComposer.LOCK_LEFTOVER_ARGB, image.pixels[0])
        assertEquals(WallpaperComposer.LOCK_LEFTOVER_ARGB, image.pixels[image.pixels.lastIndex])
        // Caption band makes the blitted block taller than a pure square QR.
        val squareOnly = WallpaperComposer.compose(matrix, 400, 800, style, applyCaption = false)
        assertTrue(image.pixels.contentEquals(squareOnly.pixels).not())
    }
}
