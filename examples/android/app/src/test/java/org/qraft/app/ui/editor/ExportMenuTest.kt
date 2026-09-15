package org.qraft.app.ui.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportMenuTest {
    @Test
    fun homeTakeawayBeforeLibraryAndDraft() {
        val items = ExportMenu.home()
        assertEquals(ExportPick.Png, items.first())
        assertTrue(items.indexOf(ExportPick.Png) < items.indexOf(ExportPick.Json))
        assertTrue(items.indexOf(ExportPick.Json) < items.indexOf(ExportPick.Duplicate))
        assertTrue(ExportPick.New in items)
        assertTrue(ExportPick.AddWidget in items)
    }

    @Test
    fun galleryOmitsDraftExceptWidget() {
        val items = ExportMenu.gallery()
        assertTrue(ExportPick.Duplicate !in items)
        assertTrue(ExportPick.New !in items)
        assertTrue(ExportPick.ImportJson !in items)
        assertTrue(ExportPick.AddWidget in items)
        assertEquals(ExportGroup.Takeaway, ExportMenu.groupOf(ExportPick.Png))
        assertEquals(ExportGroup.Library, ExportMenu.groupOf(ExportPick.Json))
        val groups = ExportMenu.grouped(ExportMenu.home())
        assertEquals(listOf(ExportGroup.Takeaway, ExportGroup.Library, ExportGroup.Draft), groups.map { it.first })
    }
}
