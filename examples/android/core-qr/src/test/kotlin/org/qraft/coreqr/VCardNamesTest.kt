package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VCardNamesTest {
    @Test
    fun structuredNUsesExplicitGivenAndFamily() {
        assertEquals("Thompson;Edward;;;", VCardNames.structuredN("Edward", "Thompson"))
        assertEquals(";Madonna;;;", VCardNames.structuredN("Madonna", ""))
        assertEquals("Lovelace;;;;", VCardNames.structuredN("", "Lovelace"))
    }

    @Test
    fun vcardPayloadIncludesNAndFnFromParts() {
        val text = QrPayload.VCard(givenName = "Edward", familyName = "Thompson", phone = "+1").encodeText()
        assertTrue(text.contains("N:Thompson;Edward;;;"))
        assertTrue(text.contains("FN:Edward Thompson"))
    }
}
