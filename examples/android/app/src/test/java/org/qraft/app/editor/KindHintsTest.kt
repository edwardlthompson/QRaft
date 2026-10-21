package org.qraft.app.editor

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.app.R

class KindHintsTest {
    @Test
    fun everyKindHasHintResource() {
        val ids = PayloadKind.entries.map { KindHints.of(it) }.toSet()
        assertTrue(ids.size >= 8)
        assertNotEquals(0, KindHints.of(PayloadKind.Url))
        assertTrue(KindHints.of(PayloadKind.Url) == R.string.kind_hint_url)
    }
}
