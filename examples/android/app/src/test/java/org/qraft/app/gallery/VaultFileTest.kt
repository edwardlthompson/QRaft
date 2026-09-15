package org.qraft.app.gallery

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VaultFileTest {
    @Test
    fun readBoundedRejectsOversizeAndEmpty() {
        val ok = "QRAFT2:abc".toByteArray()
        assertEquals(ok.toList(), VaultFile.readBounded(ByteArrayInputStream(ok))!!.toList())
        assertNull(VaultFile.readBounded(ByteArrayInputStream(ByteArray(0))))
        assertNull(VaultFile.readBounded(ByteArrayInputStream(ByteArray(8)), maxBytes = 4))
    }
}
