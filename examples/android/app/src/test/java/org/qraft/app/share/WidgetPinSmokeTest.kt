package org.qraft.app.share

import android.appwidget.AppWidgetManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.widget.WidgetPrefs
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAppWidgetManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class WidgetPinSmokeTest {
    @Test
    fun galleryAddWidgetSetsPendingProfileAndRequestsPin() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val shadow = Shadows.shadowOf(AppWidgetManager.getInstance(context)) as ShadowAppWidgetManager
        shadow.setRequestPinAppWidgetSupported(true)

        assertTrue(WidgetPin.request(context, "gallery-card-1"))
        assertEquals("gallery-card-1", WidgetPrefs.pendingId(context))
    }
}
