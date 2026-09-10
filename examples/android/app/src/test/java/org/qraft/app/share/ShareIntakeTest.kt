package org.qraft.app.share

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.qraft.app.editor.PayloadKind

class ShareIntakeTest {
    @Test
    fun urlAndEmpty() {
        assertEquals(PayloadKind.Url, ShareIntake.parse("https://example.com")?.kind)
        assertNull(ShareIntake.parse("  "))
        assertNull(ShareIntake.parse(null))
    }

    @Test
    fun vcardAndMailto() {
        val card = ShareIntake.parse("BEGIN:VCARD\nFN:Ada\nTEL:1\nEMAIL:a@b.c\nEND:VCARD", "text/vcard")
        assertEquals(PayloadKind.VCard, card?.kind)
        assertEquals("Ada", card?.primary)
        assertEquals(PayloadKind.Email, ShareIntake.parse("mailto:a@b.c")?.kind)
        assertEquals(PayloadKind.Phone, ShareIntake.parse("tel:123")?.kind)
        assertEquals(PayloadKind.Sms, ShareIntake.parse("sms:555")?.kind)
        assertEquals(PayloadKind.Wifi, ShareIntake.parse("WIFI:T:WPA;S:Home;P:x;;")?.kind)
        assertEquals(PayloadKind.Text, ShareIntake.parse("geo:1,2")?.kind)
    }

    @Test
    fun truncatesHugePaste() {
        val text = "x".repeat(ShareIntake.MAX_CHARS + 50)
        assertEquals(ShareIntake.MAX_CHARS, ShareIntake.parse(text)?.primary?.length)
    }

    @Test
    fun sendVcardExtraMapsKind() {
        val draft = ShareIntents.fromIntent(
            android.content.Intent.ACTION_SEND,
            "text/vcard",
            "BEGIN:VCARD\nFN:Ada\nTEL:1\nEMAIL:a@b.c\nEND:VCARD",
            null,
        )
        assertEquals(PayloadKind.VCard, draft?.kind)
        assertEquals("Ada", draft?.primary)
    }
}
