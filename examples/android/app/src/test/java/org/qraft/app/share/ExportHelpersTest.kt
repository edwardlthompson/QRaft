package org.qraft.app.share

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportHelpersTest {
    @Test
    fun pngSizesAreSquarePresets() {
        assertEquals(1024, ExportPngSize.DEFAULT.px)
        assertTrue(ExportPngSize.entries.all { it.px >= 512 })
    }
}
