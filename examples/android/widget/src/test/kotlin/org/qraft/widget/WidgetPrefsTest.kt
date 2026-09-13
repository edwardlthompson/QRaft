package org.qraft.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class WidgetPrefsTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun cycleWalksIds() {
        WidgetPrefs.setSelectedId(context, "a", 3)
        WidgetPrefs.cycle(context, listOf("a", "b", "c"), 3)
        assertEquals("b", WidgetPrefs.selectedId(context, 3))
        WidgetPrefs.cycle(context, listOf("a", "b", "c"), 3)
        assertEquals("c", WidgetPrefs.selectedId(context, 3))
        WidgetPrefs.cycle(context, listOf("a", "b", "c"), 3)
        assertEquals("a", WidgetPrefs.selectedId(context, 3))
    }

    @Test
    fun cyclePrevWrapsAround() {
        WidgetPrefs.setSelectedId(context, "a", 4)
        WidgetPrefs.cycle(context, listOf("a", "b", "c"), 4, delta = -1)
        assertEquals("c", WidgetPrefs.selectedId(context, 4))
        WidgetPrefs.cycle(context, listOf("a", "b", "c"), 4, delta = -1)
        assertEquals("b", WidgetPrefs.selectedId(context, 4))
    }

    @Test
    fun carouselDoesNotBleedAcrossWidgets() {
        assertFalse(WidgetPrefs.carouselEnabled(context, 7))
        WidgetPrefs.setCarouselEnabled(context, true, 7)
        assertFalse(WidgetPrefs.carouselEnabled(context, 8))
        WidgetPrefs.setCarouselEnabled(context, false, 8)
        assertTrue(WidgetPrefs.carouselEnabled(context, 7))
        assertFalse(WidgetPrefs.carouselEnabled(context, 8))
        // Unconfigured id stays off even after other widgets enable carousel.
        assertFalse(WidgetPrefs.carouselEnabled(context, 99))
    }

    @Test
    fun perWidgetIdsAreIndependent() {
        WidgetPrefs.setSelectedId(context, "one", 11)
        WidgetPrefs.setSelectedId(context, "two", 22)
        assertEquals("one", WidgetPrefs.selectedId(context, 11))
        assertEquals("two", WidgetPrefs.selectedId(context, 22))
    }

    @Test
    fun transparentAndSensitiveArePerWidget() {
        WidgetPrefs.setTransparentBg(context, true, 11)
        WidgetPrefs.setTransparentBg(context, false, 22)
        WidgetPrefs.setSensitiveLock(context, false, 11)
        WidgetPrefs.setSensitiveLock(context, true, 22)
        assertTrue(WidgetPrefs.transparentBg(context, 11))
        assertFalse(WidgetPrefs.transparentBg(context, 22))
        assertFalse(WidgetPrefs.sensitiveLock(context, 11))
        assertTrue(WidgetPrefs.sensitiveLock(context, 22))
        assertFalse(WidgetPrefs.transparentBg(context, 99))
        assertTrue(WidgetPrefs.sensitiveLock(context, 99))
    }

    @Test
    fun perWidgetCaptionIndependent() {
        WidgetPrefs.setCaptionEnabled(context, true, 11)
        WidgetPrefs.setCaptionEnabled(context, false, 22)
        assertTrue(WidgetPrefs.captionEnabled(context, 11))
        assertFalse(WidgetPrefs.captionEnabled(context, 22))
    }

    @Test
    fun consumePendingBindsGalleryCardToNewWidget() {
        WidgetPrefs.setPendingId(context, "from-gallery")
        WidgetPrefs.consumePendingIfNeeded(context, 42)
        assertEquals("from-gallery", WidgetPrefs.selectedId(context, 42))
        assertEquals(null, WidgetPrefs.pendingId(context))
        WidgetPrefs.setPendingId(context, "other")
        WidgetPrefs.consumePendingIfNeeded(context, 42)
        assertEquals("from-gallery", WidgetPrefs.selectedId(context, 42))
    }
}
