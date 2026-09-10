package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DraftHistoryTest {
    @Test
    fun undoRestoresPreviousDraft() {
        val a = DraftSnap(EditorDraft(primary = "a"), "{}", "A")
        val b = DraftSnap(EditorDraft(primary = "b"), "{}", "B")
        val (past, future) = DraftHistory.push(listOf(a), emptyList(), b)
        val (undone, past2, future2) = DraftHistory.undo(past, future)
        assertEquals("a", undone?.draft?.primary)
        val (redone, _, _) = DraftHistory.redo(past2, future2)
        assertEquals("b", redone?.draft?.primary)
    }

    @Test
    fun undoWithOneSnapStays() {
        val a = DraftSnap(EditorDraft(primary = "a"), "{}", "A")
        val (undone, past, future) = DraftHistory.undo(listOf(a), emptyList())
        assertEquals("a", undone?.draft?.primary)
        assertEquals(1, past.size)
        assertEquals(0, future.size)
        assertNull(DraftHistory.undo(emptyList(), emptyList()).first)
    }
}
