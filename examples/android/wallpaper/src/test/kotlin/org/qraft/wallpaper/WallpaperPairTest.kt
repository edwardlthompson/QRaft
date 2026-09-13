package org.qraft.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.render.QrStyle

class WallpaperPairTest {
    @Test
    fun darkVariantSwapsBodyColors() {
        val style = QrStyle(
            foregroundArgb = 0xFF000000.toInt(),
            backgroundArgb = 0xFFFFFFFF.toInt(),
            eyeColorArgb = 0xFF000000.toInt(),
        )
        val dark = WallpaperPair.darkVariant(style)
        assertEquals(0xFFFFFFFF.toInt(), dark.foregroundArgb)
        assertEquals(0xFF000000.toInt(), dark.backgroundArgb)
        assertEquals("home", WallpaperPair.previewLabel(true))
        assertEquals("lock", WallpaperPair.previewLabel(false))
    }
}

class WallpaperHistoryNamesTest {
    @Test
    fun flagsCoverTargets() {
        assertTrue(WallpaperBinder.flagsFor(WallpaperTarget.BOTH) != 0)
        assertFalse(WallpaperBinder.flagsFor(WallpaperTarget.HOME) == 0)
    }
}
