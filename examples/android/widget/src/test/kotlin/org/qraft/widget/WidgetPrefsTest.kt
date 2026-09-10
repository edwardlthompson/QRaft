package org.qraft.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
        WidgetPrefs.setSelectedId(context, "a")
        WidgetPrefs.cycle(context, listOf("a", "b", "c"))
        assertEquals("b", WidgetPrefs.selectedId(context))
        WidgetPrefs.cycle(context, listOf("a", "b", "c"))
        assertEquals("c", WidgetPrefs.selectedId(context))
        WidgetPrefs.cycle(context, listOf("a", "b", "c"))
        assertEquals("a", WidgetPrefs.selectedId(context))
    }

    @Test
    fun perWidgetIdsAreIndependent() {
        WidgetPrefs.setSelectedId(context, "one", 11)
        WidgetPrefs.setSelectedId(context, "two", 22)
        assertEquals("one", WidgetPrefs.selectedId(context, 11))
        assertEquals("two", WidgetPrefs.selectedId(context, 22))
    }

    @Test
    fun transparentBgDefaultsOn() {
        assertEquals(true, WidgetPrefs.transparentBg(context))
        WidgetPrefs.setTransparentBg(context, false)
        assertEquals(false, WidgetPrefs.transparentBg(context))
    }
}
