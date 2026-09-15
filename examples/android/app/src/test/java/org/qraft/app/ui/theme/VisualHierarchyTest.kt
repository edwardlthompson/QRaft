package org.qraft.app.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test
import org.qraft.app.ui.chrome.ChromeSelected

class VisualHierarchyTest {
    @Test
    fun previewUsesCardElevation() {
        assertEquals(ElevationLevel2, VisualHierarchy.PreviewElevation)
    }

    @Test
    fun scanFinderUsesSpacingSm() {
        assertEquals(SpacingSm, VisualHierarchy.ScanFinderPad)
    }

    @Test
    fun filterStateDescriptionExpandedCollapsed() {
        assertEquals("open", ChromeSelected.stateDescription(true, "open", "shut"))
        assertEquals("shut", ChromeSelected.stateDescription(false, "open", "shut"))
    }
}
