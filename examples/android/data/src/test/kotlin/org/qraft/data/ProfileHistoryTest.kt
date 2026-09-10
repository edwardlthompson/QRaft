package org.qraft.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileHistoryTest {
    @Test
    fun pushCapsAtMaxAndUndoRestoresLast() {
        val snaps = (1..10).map { ProfileSnapshot("p$it", "{}", it.toLong()) }
        var hist = emptyList<ProfileSnapshot>()
        snaps.forEach { hist = ProfileHistory.push(hist, it) }
        assertEquals(ProfileHistory.MAX, hist.size)
        assertEquals("p10", hist.last().payloadText)
        val (last, rest) = ProfileHistory.undo(hist)
        assertEquals("p10", last?.payloadText)
        assertEquals(ProfileHistory.MAX - 1, rest.size)
        val (emptyUndo, stillEmpty) = ProfileHistory.undo(emptyList())
        assertNull(emptyUndo)
        assertTrue(stillEmpty.isEmpty())
    }
}
