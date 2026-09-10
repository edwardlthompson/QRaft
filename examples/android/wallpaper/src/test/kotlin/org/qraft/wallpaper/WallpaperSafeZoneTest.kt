package org.qraft.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperSafeZoneTest {
    @Test
    fun defaultTenPercentMargin() {
        val safe = WallpaperSafeZone.safeRectangle(1000, 2000, 0.10)
        // Width_safe = 1000 * 0.9 → 900; leftover 100 split as 50 per side
        assertEquals(50, safe.left)
        assertEquals(100, safe.top)
        assertEquals(900, safe.width)
        assertEquals(1800, safe.height)
    }

    @Test
    fun qrContentIsSquareInsideSafeZone() {
        val qr = WallpaperSafeZone.qrContentRect(1080, 2400, 0.10)
        assertEquals(qr.width, qr.height)
        val safe = WallpaperSafeZone.safeRectangle(1080, 2400, 0.10)
        assertTrue(qr.left >= safe.left)
        assertTrue(qr.top >= safe.top)
        assertTrue(qr.right <= safe.right)
        assertTrue(qr.bottom <= safe.bottom)
    }

    @Test
    fun userMarginClampsToTwentyPercent() {
        assertEquals(0.0, WallpaperSafeZone.clampUserMargin(-1.0), 0.0)
        assertEquals(0.20, WallpaperSafeZone.clampUserMargin(0.5), 0.0)
        val qr = WallpaperSafeZone.qrContentRect(1000, 2000, 0.9)
        val atTwenty = WallpaperSafeZone.qrContentRect(1000, 2000, 0.20)
        assertEquals(atTwenty, qr)
    }
}
