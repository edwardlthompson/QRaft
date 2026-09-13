package org.qraft.app.wifi

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class CurrentWifiTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun needsLocationWithoutGrantOnApi29() {
        assertTrue(CurrentWifi.needsLocationPermission(context))
        assertEquals(null, CurrentWifi.ssidOrNull(context))
    }

    @Test
    fun locationPermissionConstantIsFineOnApi29() {
        assertEquals(Manifest.permission.ACCESS_FINE_LOCATION, CurrentWifi.locationPermission)
    }

    @Test
    fun grantedPermissionClearsNeedFlag() {
        Shadows.shadowOf(RuntimeEnvironment.getApplication())
            .grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        assertEquals(false, CurrentWifi.needsLocationPermission(context))
    }
}
