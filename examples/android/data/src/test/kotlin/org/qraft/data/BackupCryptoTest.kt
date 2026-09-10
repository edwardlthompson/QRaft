package org.qraft.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupCryptoTest {
    @Test
    fun blankPassphraseStaysPlain() {
        assertEquals("[1]", BackupCrypto.wrap("[1]", "  "))
        assertEquals("[1]", BackupCrypto.unwrap("[1]", ""))
    }

    @Test
    fun roundTripWithPassphrase() {
        val wrapped = BackupCrypto.wrap("""[{"id":"a"}]""", "secret")
        assertTrue(wrapped.startsWith("QRAFT1:"))
        assertEquals("""[{"id":"a"}]""", BackupCrypto.unwrap(wrapped, "secret"))
        assertNull(BackupCrypto.unwrap(wrapped, "wrong"))
        assertNull(BackupCrypto.unwrap(wrapped, ""))
    }
}
