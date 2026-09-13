package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StylePresetsTest {
    @Test
    fun packsAreOfflineAndNonEmpty() {
        assertTrue(StylePresets.ALL.size >= 4)
        assertEquals("classic", StylePresets.byId("classic")?.id)
        assertTrue(StylePresets.ALL.none { it.style.logoImagePath.isNotBlank() })
    }
}
