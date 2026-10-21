package org.qraft.app.ui.adaptive

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QraftWidthClassTest {
    @Test
    fun from_mapsMaterialWidthClasses() {
        assertEquals(QraftWidthClass.Compact, QraftWidthClass.from(WindowWidthSizeClass.Compact))
        assertEquals(QraftWidthClass.Medium, QraftWidthClass.from(WindowWidthSizeClass.Medium))
        assertEquals(QraftWidthClass.Expanded, QraftWidthClass.from(WindowWidthSizeClass.Expanded))
    }

    @Test
    fun isTwoPane_onlyMediumAndExpanded() {
        assertFalse(QraftWidthClass.Compact.isTwoPane)
        assertTrue(QraftWidthClass.Medium.isTwoPane)
        assertTrue(QraftWidthClass.Expanded.isTwoPane)
    }
}
