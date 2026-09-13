package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrPayloadSprint12Test {
    @Test
    fun calendarEventEncodesVevent() {
        val text = QrPayload.CalendarEvent(
            summary = "Standup",
            dtStart = "20260315T090000",
            location = "Room A",
            description = "Daily sync",
        ).encodeText()
        assertTrue(text.startsWith("BEGIN:VEVENT"))
        assertTrue(text.contains("SUMMARY:Standup"))
        assertTrue(text.contains("DTSTART:20260315T090000"))
        assertTrue(text.contains("LOCATION:Room A"))
        assertTrue(text.contains("DESCRIPTION:Daily sync"))
        assertTrue(text.endsWith("END:VEVENT"))
    }

    @Test
    fun geoEncodesLatLon() {
        assertEquals("GEO:37.7749,-122.4194", QrPayload.Geo("37.7749", "-122.4194").encodeText())
    }

    @Test
    fun whatsAppEncodesWaMe() {
        assertEquals("https://wa.me/15555550100", QrPayload.WhatsApp("+1 555-555-0100").encodeText())
        assertTrue(
            QrPayload.WhatsApp("15555550100", "Hello").encodeText()
                .startsWith("https://wa.me/15555550100?text="),
        )
    }

    @Test
    fun whatsAppStripsExistingWaMeUrl() {
        assertEquals(
            "https://wa.me/15555550100",
            QrPayload.WhatsApp("https://wa.me/15555550100?text=hi").encodeText(),
        )
    }

    @Test
    fun appStoreAndSocialAreUrls() {
        val fdroid = "https://f-droid.org/packages/org.qraft.app/"
        assertEquals(fdroid, QrPayload.AppStore(fdroid).encodeText())
        val matrix = "https://matrix.to/#/#room:example.org"
        assertEquals(matrix, QrPayload.SocialUrl(matrix).encodeText())
    }

    @Test
    fun meCardAndFaceTimeEncode() {
        assertEquals(
            "MECARD:N:Ada;TEL:+1;EMAIL:a@b.c;;",
            QrPayload.MeCard("Ada", "+1", "a@b.c").encodeText(),
        )
        assertEquals("facetime:hello@example.com", QrPayload.FaceTime("hello@example.com").encodeText())
        assertEquals(
            "facetime-audio:hello@example.com",
            QrPayload.FaceTime("hello@example.com", audio = true).encodeText(),
        )
    }
}
