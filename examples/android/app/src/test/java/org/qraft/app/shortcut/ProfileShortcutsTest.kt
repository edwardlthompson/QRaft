package org.qraft.app.shortcut

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.qraft.data.QrProfile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ProfileShortcutsTest {
    @Test
    fun parsesNewAndLastIntents() {
        assertTrue(ProfileShortcuts.wantsNewQr(Intent(ProfileShortcuts.ACTION_NEW)))
        assertTrue(ProfileShortcuts.wantsLastCard(Intent(Intent.ACTION_VIEW, Uri.parse("qraft://profile/last"))))
        assertNull(ProfileShortcuts.profileIdFromIntent(Intent(Intent.ACTION_VIEW, Uri.parse("qraft://profile/last"))))
        assertEquals(
            "abc",
            ProfileShortcuts.profileIdFromIntent(Intent(Intent.ACTION_VIEW, Uri.parse("qraft://profile/abc"))),
        )
        assertFalse(ProfileShortcuts.wantsNewQr(Intent(Intent.ACTION_VIEW, Uri.parse("qraft://profile/abc"))))
    }

    @Test
    fun remembersLastProfileId() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        ProfileShortcuts.publishOpen(
            context,
            QrProfile("pid", "Name", "https://example.com", listOf("t"), "", false, 1L),
        )
        assertEquals("pid", ProfileShortcuts.lastProfileId(context))
    }
}
