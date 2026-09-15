package org.qraft.app.gallery

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.app.editor.PayloadKind
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ScanHomeSeedTest {
    @Test
    fun wifiAndUrlMatchGalleryApply() {
        val wifi = ScanHomeSeed.apply("WIFI:T:WPA;S:Cafe;P:x;;")
        assertEquals(PayloadKind.Wifi, wifi.first.kind)
        assertEquals("Cafe", wifi.third)
        val url = ScanHomeSeed.apply("https://example.com/scan")
        assertEquals(PayloadKind.Url, url.first.kind)
        assertEquals("https://example.com/scan", url.first.primary)
    }
}
