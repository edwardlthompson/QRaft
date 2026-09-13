package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrPayload
import org.qraft.data.QrProfile

class VCardDraftTest {
    @Test
    fun fromPayloadSplitsNIntoFirstAndLast() {
        val body = """
            BEGIN:VCARD
            VERSION:3.0
            N:Thompson;Edward;;;
            FN:Edward Thompson
            TEL:+1
            EMAIL:e@example.com
            ORG:QRaft
            URL:https://example.com
            END:VCARD
        """.trimIndent()
        val draft = VCardDraft.fromPayloadText(body)
        assertEquals(PayloadKind.VCard, draft.kind)
        assertEquals("Edward", draft.givenName)
        assertEquals("Thompson", draft.familyName)
        assertEquals("+1", draft.secondary)
        assertEquals("e@example.com", draft.tertiary)
        assertEquals("QRaft", draft.org)
        assertEquals("https://example.com", draft.url)
        assertFalse(draft.givenName.startsWith("BEGIN:"))
    }

    @Test
    fun profileApplyDoesNotPutBlobInName() {
        val profile = QrProfile(
            id = "v1",
            name = "Card",
            payloadText = QrPayload.VCard("Ada", "Lovelace", phone = "1", email = "a@b.c").encodeText(),
        )
        val draft = ProfileApply.fromProfile(profile).first
        assertEquals("Ada", draft.givenName)
        assertEquals("Lovelace", draft.familyName)
        assertEquals("1", draft.secondary)
        assertEquals("a@b.c", draft.tertiary)
    }

    @Test
    fun recoversWhenFnWasCorruptedToBeginVcard() {
        val body = """
            BEGIN:VCARD
            VERSION:3.0
            N:Lovelace;Ada;;;
            FN:BEGIN:VCARD
            TEL:9
            END:VCARD
        """.trimIndent()
        val draft = VCardDraft.fromPayloadText(body)
        assertEquals("Ada", draft.givenName)
        assertEquals("Lovelace", draft.familyName)
    }

    @Test
    fun toPayloadUsesGivenAndFamily() {
        val draft = EditorDraft(
            kind = PayloadKind.VCard,
            givenName = "Edward",
            familyName = "Thompson",
        )
        val encoded = draft.toPayload()!!.encodeText()
        assertEquals("Edward", VCardDraft.fromPayloadText(encoded).givenName)
        assertEquals("Thompson", VCardDraft.fromPayloadText(encoded).familyName)
        assertTrue(encoded.contains("N:Thompson;Edward;;;"))
    }
}
