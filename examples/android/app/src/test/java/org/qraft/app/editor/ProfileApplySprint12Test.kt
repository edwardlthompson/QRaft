package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Test
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle

class ProfileApplySprint12Test {
    @Test
    fun meCardRoundTripsFromProfile() {
        val profile = QrProfile(
            id = "1",
            name = "Ada",
            payloadText = "MECARD:N:Ada;TEL:+1;EMAIL:a@b.c;;",
            tags = listOf("Contact"),
            styleJson = "",
            sensitive = false,
            updatedAt = 1L,
        )
        val draft = ProfileApply.fromProfile(profile).first
        assertEquals(PayloadKind.MeCard, draft.kind)
        assertEquals("Ada", draft.primary)
        assertEquals("+1", draft.secondary)
        assertEquals("a@b.c", draft.tertiary)
    }

    @Test
    fun faceTimeAudioFromProfile() {
        val profile = QrProfile(
            id = "2",
            name = "Call",
            payloadText = "facetime-audio:hello@example.com",
            tags = listOf("Phone"),
            styleJson = "",
            sensitive = false,
            updatedAt = 1L,
        )
        val draft = ProfileApply.fromProfile(profile).first
        assertEquals(PayloadKind.FaceTime, draft.kind)
        assertEquals("hello@example.com", draft.primary)
        assertEquals("audio", draft.secondary)
        assertEquals(QrStyle.DEFAULT, ProfileApply.fromProfile(profile).second)
    }
}
