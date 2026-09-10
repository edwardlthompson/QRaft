package org.qraft.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class QrWidgetCacheTest {
    @Test
    fun missIsNull() {
        assertNull(QrWidgetCache().get("missing"))
    }

    @Test
    fun roundTripCopiesPixels() {
        val cache = QrWidgetCache()
        val key = cache.key("home", "{}", 64)
        val pixels = IntArray(64 * 64) { 0xFF000000.toInt() }
        cache.put(key, 64, 64, pixels)
        pixels[0] = 0xFFFFFFFF.toInt()
        val hit = cache.get(key)!!
        assertEquals(64, hit.width)
        assertEquals(0xFF000000.toInt(), hit.pixels[0])
    }

    @Test
    fun rejectsBlankOrMismatchedBuffers() {
        val cache = QrWidgetCache()
        cache.put("", 8, 8, IntArray(64))
        cache.put("k", 8, 8, IntArray(4))
        assertNull(cache.get(""))
        assertNull(cache.get("k"))
    }
}

class BrightenActionTest {
    @Test
    fun windowBrightnessIsMax() {
        assertEquals("org.qraft.widget.action.BRIGHTEN", BrightenAction.ACTION)
        assertEquals(1.0f, BrightenAction.WINDOW_BRIGHTNESS, 0.0f)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class BrightenIntentsTest {
    @Test
    fun usesProfilePayloadAndNewTask() {
        val profile = org.qraft.data.QrProfile("id", "Work", "https://fdroid.org", styleJson = "{}")
        val intent = BrightenIntents.activityIntent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            profile,
        )
        assertEquals(BrightenActivity::class.java.name, intent.component?.className)
        assertEquals("https://fdroid.org", intent.getStringExtra(BrightenActivity.EXTRA_PAYLOAD))
        assertEquals("{}", intent.getStringExtra(BrightenActivity.EXTRA_STYLE))
        assertTrue(intent.flags and android.content.Intent.FLAG_ACTIVITY_NEW_TASK != 0)
    }

    @Test
    fun nullProfileFallsBackToSeed() {
        val intent = BrightenIntents.activityIntent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            null,
        )
        assertEquals(
            org.qraft.data.DataStoreProfileRepository.seedWebsite().payloadText,
            intent.getStringExtra(BrightenActivity.EXTRA_PAYLOAD),
        )
    }
}

class WidgetTalkbackTest {
    @Test
    fun descriptionOmitsWifiPassword() {
        val payload = "WIFI:T:WPA;S:Cafe;P:s3cret;"
        val description = "QR code for ${WidgetTalkback.label("Guest Wi-Fi")}"
        assertTrue(WidgetTalkback.descriptionOmitsSecrets(description, payload))
        assertEquals("s3cret", WidgetTalkback.wifiPassword(payload))
    }
}
