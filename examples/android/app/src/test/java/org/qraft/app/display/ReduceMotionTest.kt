package org.qraft.app.display

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReduceMotionTest {
    @Test
    fun animatorOffMeansReduceMotion() {
        assertTrue(ReduceMotion.enabled(0f))
        assertFalse(ReduceMotion.enabled(1f))
        assertFalse(ReduceMotion.enabled(0.5f))
        assertFalse(ReduceMotion.allowHaptic(0f))
        assertTrue(ReduceMotion.allowHaptic(1f))
        assertEquals(0, ReduceMotion.durationMs(0f, 200))
        assertEquals(200, ReduceMotion.durationMs(1f, 200))
    }
}
