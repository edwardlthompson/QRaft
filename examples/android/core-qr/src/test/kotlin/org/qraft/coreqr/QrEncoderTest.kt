package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QrEncoderTest {
    @Test
    fun encodeUrlProducesValidMatrix() {
        val matrix = QrEncoder.encode(QrPayload.Url("https://example.com"))
        assertTrue(matrix.version in 1..40)
        assertEquals(matrix.version * 4 + 17, matrix.size)
        assertTrue(matrix.isDark(0, 0)) // finder pattern corner
        assertTrue(matrix.isDark(matrix.size - 1, 0))
        assertTrue(matrix.isDark(0, matrix.size - 1))
    }

    @Test
    fun encodeEdwardlthompsonUrl() {
        val url = "https://edwardlthompson.com/"
        val matrix = QrEncoder.encode(QrPayload.Url(url))
        assertEquals(url, QrPayload.Url(url).encodeText())
        assertTrue(matrix.size >= 21)
        assertTrue(matrix.isDark(0, 0))
    }

    @Test
    fun forceHighEccUsesH() {
        val matrix = QrEncoder.encode(
            QrPayload.Text("overlay-logo"),
            errorCorrection = ErrorCorrectionLevel.L,
            forceHighEcc = true,
            boostEcl = false,
        )
        assertEquals(ErrorCorrectionLevel.H, matrix.errorCorrection)
    }

    @Test
    fun wifiPayloadFormat() {
        val text = QrPayload.Wifi("Cafe;Net", "p:ass", WifiSecurity.WPA).encodeText()
        assertTrue(text.startsWith("WIFI:T:WPA;S:Cafe\\;Net;P:p\\:ass;"))
    }

    @Test
    fun wifiHiddenNetworkEncodesH() {
        val text = QrPayload.Wifi("Cafe", hidden = true).encodeText()
        assertTrue(text.contains("H:true;"))
    }

    @Test
    fun vcardIncludesOrgAndUrl() {
        val text = QrPayload.VCard(givenName = "Ada", org = "QRaft", url = "https://qraft.app").encodeText()
        assertTrue(text.contains("ORG:QRaft"))
        assertTrue(text.contains("URL:https://qraft.app"))
    }

    @Test
    fun encodeIsDeterministic() {
        val a = QrEncoder.encodeText("https://qraft.app", ErrorCorrectionLevel.M, boostEcl = false)
        val b = QrEncoder.encodeText("https://qraft.app", ErrorCorrectionLevel.M, boostEcl = false)
        assertEquals(a, b)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyPayloadRejected() {
        QrEncoder.encode(QrPayload.Text(""))
    }
}
