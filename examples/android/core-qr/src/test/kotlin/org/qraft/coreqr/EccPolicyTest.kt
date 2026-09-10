package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EccPolicyTest {
    @Test
    fun overlayAlwaysHigh() {
        QrSurface.entries.forEach { surface ->
            assertEquals(ErrorCorrectionLevel.H, EccPolicy.choose(surface, hasOverlay = true))
            assertTrue(EccPolicy.forceHighEcc(true))
            assertFalse(EccPolicy.boostEcl(surface, hasOverlay = true))
        }
    }

    @Test
    fun wallpaperHighWithoutOverlay() {
        assertEquals(ErrorCorrectionLevel.H, EccPolicy.choose(QrSurface.WALLPAPER, false))
        assertFalse(EccPolicy.boostEcl(QrSurface.WALLPAPER, false))
    }

    @Test
    fun widgetAndEditorMediumWithBoost() {
        assertEquals(ErrorCorrectionLevel.M, EccPolicy.choose(QrSurface.WIDGET, false))
        assertEquals(ErrorCorrectionLevel.M, EccPolicy.choose(QrSurface.EDITOR, false))
        assertEquals(ErrorCorrectionLevel.M, EccPolicy.choose(QrSurface.EXPORT, false))
        assertTrue(EccPolicy.boostEcl(QrSurface.EDITOR, false))
        assertTrue(EccPolicy.boostEcl(QrSurface.WIDGET, false))
    }
}
