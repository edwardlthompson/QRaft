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

class SensitiveUnlockTest {
    @Test
    fun onlySensitiveWithLock() {
        assertTrue(SensitiveUnlock.requiresAuth(true, true))
        assertTrue(!SensitiveUnlock.requiresAuth(true, false))
        assertTrue(!SensitiveUnlock.requiresAuth(false, true))
    }
}

class WidgetCacheRefreshTest {
    @Test
    fun clearDropsEntries() {
        val cache = WidgetRefresh.sharedCache()
        cache.put("k", 2, 2, IntArray(4) { 1 })
        assertEquals(2, cache.get("k")!!.width)
        cache.clear()
        assertNull(cache.get("k"))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class BrightenTileTest {
    @Test
    fun pendingIntentTargetsBrightenActivity() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val profile = org.qraft.data.QrProfile("id", "Work", "https://fdroid.org", styleJson = "{}")
        val pending = BrightenTile.pendingIntent(context, profile)
        assertEquals(43, BrightenTile.REQUEST_CODE)
        val intent = org.robolectric.Shadows.shadowOf(pending).savedIntent
        assertEquals(BrightenActivity::class.java.name, intent.component?.className)
        assertEquals("https://fdroid.org", intent.getStringExtra(BrightenActivity.EXTRA_PAYLOAD))
    }
}
