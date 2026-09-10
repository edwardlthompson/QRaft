package org.qraft.widget

import org.junit.Assert.assertEquals
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
}

class WidgetPaintTest {
    @Test
    fun transparentClearsBackgroundAlpha() {
        val opaque = org.qraft.render.QrStyle.DEFAULT
        assertEquals(0, WidgetPaint.style(opaque, true).backgroundArgb)
        assertEquals(opaque.backgroundArgb, WidgetPaint.style(opaque, false).backgroundArgb)
    }
}
