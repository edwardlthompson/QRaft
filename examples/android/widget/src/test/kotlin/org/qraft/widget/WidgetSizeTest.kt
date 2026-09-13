package org.qraft.widget

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetSizeTest {
    @Test
    fun bucketsOneTwoThree() {
        assertEquals(1, WidgetSize.cells(40, 40))
        assertEquals(1, WidgetSize.cells(70, 110))
        assertEquals(2, WidgetSize.cells(110, 110))
        assertEquals(2, WidgetSize.cells(150, 150))
        assertEquals(3, WidgetSize.cells(180, 180))
        assertEquals(3, WidgetSize.cells(250, 180))
        assertEquals(1, WidgetSize.cells(-4, 200))
    }

    @Test
    fun chromeHiddenOnOneByOne() {
        assertFalse(WidgetSize.showChrome(DpSize(40.dp, 40.dp)))
        assertTrue(WidgetSize.showChrome(DpSize(110.dp, 110.dp)))
    }

    @Test
    fun exactPxTracksDensityAndSide() {
        val tiny = DpSize(40.dp, 40.dp)
        assertEquals(80, WidgetSize.exactPx(2f, tiny))
        val mid = DpSize(100.dp, 100.dp)
        val side = 100f - WidgetQrCanvas.CHROME_RESERVE_DP
        assertEquals(
            (side * 3f).toInt().coerceAtMost(WidgetQrCanvas.MAX_BITMAP_PX),
            WidgetSize.exactPx(3f, mid),
        )
    }

    @Test
    fun usableSideFitsMinAxisNotForcedSquareFrame() {
        // Wide widget: side follows height (minus chrome), not width.
        val wide = WidgetSize.usableSideDp(DpSize(300.dp, 100.dp))
        assertEquals(100f - WidgetQrCanvas.CHROME_RESERVE_DP, wide.value, 0.01f)
        // Tall widget: side follows width (chrome comes off height, width still smaller).
        val tall = WidgetSize.usableSideDp(DpSize(80.dp, 200.dp))
        assertEquals(80f, tall.value, 0.01f)
    }
}

class WidgetQrCanvasTest {
    @Test
    fun snapToModulesKeepsIntegerModulePixels() {
        val modules = 29
        val snapped = WidgetQrCanvas.snapToModules(200, modules)
        assertEquals(0, snapped % modules)
        assertTrue(snapped <= WidgetQrCanvas.MAX_BITMAP_PX)
    }

    @Test
    fun targetPxNeverExceedsBinderSafeCap() {
        assertEquals(WidgetQrCanvas.MIN_PX, WidgetQrCanvas.targetPx(1f, 10f))
        assertEquals(WidgetQrCanvas.MAX_BITMAP_PX, WidgetQrCanvas.targetPx(4f, 400f))
    }
}

class WidgetPaintTest {
    @Test
    fun transparentClearsBackgroundAlpha() {
        val opaque = org.qraft.render.QrStyle.DEFAULT
        assertEquals(0, WidgetPaint.style(opaque, true).backgroundArgb)
        assertEquals(opaque.backgroundArgb, WidgetPaint.style(opaque, false).backgroundArgb)
    }

    @Test
    fun opaqueForcesWhiteWhenStyleBackgroundIsClear() {
        val clear = org.qraft.render.QrStyle.DEFAULT.copy(backgroundArgb = 0)
        assertEquals(0xFFFFFFFF.toInt(), WidgetPaint.style(clear, false).backgroundArgb)
    }
}
