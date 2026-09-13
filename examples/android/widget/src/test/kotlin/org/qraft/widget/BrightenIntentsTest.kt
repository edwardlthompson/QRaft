package org.qraft.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class BrightenIntentsTest {
    @Test
    fun usesProfilePayloadAndNewTask() {
        val profile = org.qraft.data.QrProfile("id", "Work", "https://fdroid.org", styleJson = "{}")
        val intent = BrightenIntents.activityIntent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            profile,
            lockEnabled = false,
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
            lockEnabled = false,
        )
        assertEquals(
            org.qraft.data.DataStoreProfileRepository.seedWebsite().payloadText,
            intent.getStringExtra(BrightenActivity.EXTRA_PAYLOAD),
        )
    }

    @Test
    fun sensitiveOmitsPayloadUntilUnlock() {
        val profile = org.qraft.data.QrProfile(
            "sec",
            "Wi-Fi",
            "WIFI:T:WPA;S:Cafe;P:s3cret;;",
            styleJson = "{}",
            sensitive = true,
        )
        val intent = BrightenIntents.activityIntent(
            androidx.test.core.app.ApplicationProvider.getApplicationContext(),
            profile,
            lockEnabled = true,
        )
        assertTrue(intent.getBooleanExtra(BrightenActivity.EXTRA_REQUIRES_AUTH, false))
        assertEquals("sec", intent.getStringExtra(BrightenActivity.EXTRA_PROFILE_ID))
        assertNull(intent.getStringExtra(BrightenActivity.EXTRA_PAYLOAD))
    }
}
