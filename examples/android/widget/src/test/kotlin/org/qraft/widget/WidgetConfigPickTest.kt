package org.qraft.widget

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetConfigPickTest {
    @Test
    fun nullSelectionShowsError() {
        assertTrue(WidgetConfigPick.showPickError(null))
        assertFalse(WidgetConfigPick.showPickError("card-1"))
    }
}
