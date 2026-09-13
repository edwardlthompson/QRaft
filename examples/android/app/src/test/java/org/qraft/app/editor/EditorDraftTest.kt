package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.EccPolicy
import org.qraft.coreqr.ErrorCorrectionLevel
import org.qraft.coreqr.QrPayload
import org.qraft.coreqr.QrSurface
import org.qraft.coreqr.WifiSecurity

class EditorDraftTest {
    @Test
    fun urlDraftEncodesTrimmed() {
        val payload = EditorDraft(primary = "  https://edwardlthompson.com/  ").toPayload()
        assertEquals("https://edwardlthompson.com/", (payload as QrPayload.Url).encodeText())
    }

    @Test
    fun emptyPrimaryIsNull() {
        assertNull(EditorDraft(kind = PayloadKind.Text, primary = "  ").toPayload())
        assertNull(EditorDraft(kind = PayloadKind.Wifi, primary = "").toPayload())
    }

    @Test
    fun wifiMapsSecurityAndPassword() {
        val payload = EditorDraft(
            kind = PayloadKind.Wifi,
            primary = "Cafe",
            secondary = "secret",
            wifiSecurity = WifiSecurity.WPA,
        ).toPayload() as QrPayload.Wifi
        assertTrue(payload.encodeText().startsWith("WIFI:T:WPA;S:Cafe;"))
        assertTrue(payload.encodeText().contains("P:secret"))
    }

    @Test
    fun wifiHiddenNetworkFlag() {
        val payload = EditorDraft(
            kind = PayloadKind.Wifi,
            primary = "Cafe",
            wifiHidden = true,
        ).toPayload() as QrPayload.Wifi
        assertTrue(payload.encodeText().contains("H:true;"))
    }

    @Test
    fun vcardOrgAndUrlFields() {
        val text = EditorDraft(
            kind = PayloadKind.VCard,
            givenName = "Ada",
            familyName = "Lovelace",
            secondary = "555",
            tertiary = "ada@example.com",
            org = "QRaft",
            url = "https://qraft.app",
        ).toPayload()!!.encodeText()
        assertTrue(text.contains("ORG:QRaft"))
        assertTrue(text.contains("URL:https://qraft.app"))
        assertTrue(text.contains("N:Lovelace;Ada;;;"))
    }

    @Test
    fun cryptoSchemePickerEncodesUri() {
        val payload = EditorDraft(
            kind = PayloadKind.Crypto,
            primary = "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            secondary = "bitcoin",
        ).toPayload() as QrPayload.CryptoAddress
        assertEquals(
            "bitcoin:bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            payload.encodeText(),
        )
        assertEquals(CryptoScheme.Bitcoin, CryptoScheme.fromWire("BITCOIN"))
        assertEquals(CryptoScheme.None, CryptoScheme.fromWire("solana"))
    }

    @Test
    fun defaultPrimaryPerKind() {
        assertEquals(EditorDraft.DEFAULT_URL, EditorDraft.defaultPrimary(PayloadKind.Url))
        assertEquals(EditorDraft.DEFAULT_TEXT, EditorDraft.defaultPrimary(PayloadKind.Text))
        assertEquals(EditorDraft.DEFAULT_WIFI, EditorDraft.defaultPrimary(PayloadKind.Wifi))
        assertEquals(EditorDraft.DEFAULT_GIVEN, EditorDraft.defaultPrimary(PayloadKind.VCard))
        assertEquals(EditorDraft.DEFAULT_EMAIL, EditorDraft.defaultPrimary(PayloadKind.Email))
        assertEquals(EditorDraft.DEFAULT_PHONE, EditorDraft.defaultPrimary(PayloadKind.Sms))
        assertEquals(EditorDraft.DEFAULT_PHONE, EditorDraft.defaultPrimary(PayloadKind.Phone))
        assertEquals("", EditorDraft.defaultPrimary(PayloadKind.Crypto))
        assertEquals(EditorDraft.DEFAULT_VCARD, EditorDraft.defaultPrimary(PayloadKind.MeCard))
        assertEquals(EditorDraft.DEFAULT_EMAIL, EditorDraft.defaultPrimary(PayloadKind.FaceTime))
    }

    @Test
    fun meCardAndFaceTimeDraftsEncode() {
        val me = EditorDraft(
            kind = PayloadKind.MeCard,
            primary = "Ada",
            secondary = "+1",
            tertiary = "a@b.c",
        ).toPayload()!!.encodeText()
        assertEquals("MECARD:N:Ada;TEL:+1;EMAIL:a@b.c;;", me)
        assertEquals(
            "facetime-audio:hello@example.com",
            EditorDraft(
                kind = PayloadKind.FaceTime,
                primary = "hello@example.com",
                secondary = "audio",
            ).toPayload()!!.encodeText(),
        )
    }

    @Test
    fun overlayUsesPolicyHigh() {
        assertEquals(ErrorCorrectionLevel.M, EccPolicy.choose(QrSurface.EDITOR, false))
        assertEquals(ErrorCorrectionLevel.H, EccPolicy.choose(QrSurface.EDITOR, true))
    }

    @Test
    fun autoCenterMarkFollowsPayloadKind() {
        assertEquals(
            org.qraft.render.CenterMark.LINK,
            RasterExtrasFactory.resolve(org.qraft.render.CenterMark.AUTO, PayloadKind.Url),
        )
        assertEquals(
            org.qraft.render.CenterMark.WIFI,
            RasterExtrasFactory.resolve(org.qraft.render.CenterMark.AUTO, PayloadKind.Wifi),
        )
    }
}
