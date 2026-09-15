package org.qraft.app.editor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DraftDirtyTest {
    @Test
    fun emptyPrimaryIsNotDirty() {
        assertFalse(DraftDirty.isDirty(EditorDraft(primary = ""), emptyList()))
        assertFalse(DraftDirty.isDirty(EditorDraft(primary = "  "), emptyList()))
    }

    @Test
    fun factoryDefaultIsNotDirty() {
        assertFalse(DraftDirty.isDirty(EditorDraft(), emptyList()))
        assertFalse(DraftDirty.isDirty(EditorDraft.withKind(PayloadKind.Wifi), emptyList()))
    }

    @Test
    fun customUnsavedIsDirty() {
        val draft = EditorDraft(primary = "https://github.com/qraft")
        assertTrue(DraftDirty.isDirty(draft, emptyList()))
        assertTrue(DraftDirty.isDirty(draft, listOf("https://example.com")))
    }

    @Test
    fun matchingSavedCardIsNotDirty() {
        val url = "https://github.com/qraft"
        val draft = EditorDraft(primary = url)
        assertFalse(DraftDirty.isDirty(draft, listOf(url)))
        assertFalse(DraftDirty.isDirty(draft, listOf("https://other", url)))
    }
}
