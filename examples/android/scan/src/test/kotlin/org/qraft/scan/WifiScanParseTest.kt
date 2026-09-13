package org.qraft.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WifiScanParseTest {
    @Test
    fun parsesWpaFields() {
        val wifi = WifiScanParse.parse("WIFI:T:WPA;S:Cafe\\;Net;P:secr\\;et;H:true;")!!
        assertEquals("Cafe;Net", wifi.ssid)
        assertEquals("secr;et", wifi.password)
        assertEquals("WPA", wifi.security)
        assertTrue(wifi.hidden)
    }

    @Test
    fun rejectsNonWifi() {
        assertNull(WifiScanParse.parse("https://example.com"))
    }
}

class ScanActionsKindTest {
    @Test
    fun classifiesPayloads() {
        assertEquals(ScanActionKind.Open, ScanActions.kind("https://a.org"))
        assertEquals(ScanActionKind.JoinWifi, ScanActions.kind("WIFI:T:WPA;S:x;P:y;;"))
        assertEquals(ScanActionKind.Copy, ScanActions.kind("hello"))
        assertEquals(ScanActionKind.None, ScanActions.kind("  "))
        assertTrue(ScanActions.canOpen("mailto:a@b.c"))
        assertFalse(ScanActions.canOpen("WIFI:T:nopass;S:x;;"))
    }
}
