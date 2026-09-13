package org.qraft.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class WidgetTalkbackTest {
    @Test
    fun descriptionOmitsWifiPassword() {
        val payload = "WIFI:T:WPA;S:Cafe;P:s3cret;"
        val description = "QR code for ${WidgetTalkback.talkbackName("Guest Wi-Fi", payload)}"
        assertTrue(WidgetTalkback.descriptionOmitsSecrets(description, payload))
        assertEquals("s3cret", WidgetTalkback.wifiPassword(payload))
        assertEquals("Wi-Fi", WidgetTalkback.kindLabel(payload))
        assertTrue(!description.contains("s3cret"))
    }

    @Test
    fun kindLabelsCoverCommonPayloads() {
        assertEquals("Website", WidgetTalkback.kindLabel("https://fdroid.org"))
        assertEquals("Contact", WidgetTalkback.kindLabel("BEGIN:VCARD\nFN:Ada\nEND:VCARD"))
        assertEquals("Crypto", WidgetTalkback.kindLabel("bitcoin:bc1qexample"))
        assertEquals("Location", WidgetTalkback.kindLabel("GEO:1.0,2.0"))
    }
}

class WidgetCaptionTest {
    @Test
    fun prefersStyleCaptionOverName() {
        val profile = org.qraft.data.QrProfile(
            "1",
            "Card",
            "https://a.test",
            styleJson = "{\"caption\":\"Scan me\"}",
        )
        assertEquals("Scan me", WidgetCaption.text(profile))
        assertEquals("Scan me", WidgetCaption.forBitmap(profile, nameFallback = false))
        assertEquals("Scan me", WidgetCaption.forBitmap(profile, nameFallback = true))
    }

    @Test
    fun fallsBackToNameWhenAllowed() {
        val profile = org.qraft.data.QrProfile("1", "Work", "https://a.test", styleJson = "{}")
        assertEquals("Work", WidgetCaption.text(profile))
        assertEquals("", WidgetCaption.forBitmap(profile, nameFallback = false))
        assertEquals("Work", WidgetCaption.forBitmap(profile, nameFallback = true))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class WidgetPinHelperTest {
    @Test
    fun exposesDistinctHomeAndKeyguardCopy() {
        assertTrue(WidgetPinHelper.homeRes != 0)
        assertTrue(WidgetPinHelper.keyguardRes != 0)
        assertTrue(WidgetPinHelper.homeRes != WidgetPinHelper.keyguardRes)
    }
}
