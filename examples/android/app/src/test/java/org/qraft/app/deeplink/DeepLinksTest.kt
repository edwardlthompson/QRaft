package org.qraft.app.deeplink

import android.content.Intent
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DeepLinksTest {
    @Test
    fun parsesProfilePath() {
        val uri = Uri.parse("qraft://profile/abc-123")
        assertEquals("abc-123", DeepLinks.profileIdFromUri(uri))
        assertEquals(uri, DeepLinks.profileUri("abc-123"))
    }

    @Test
    fun rejectsOtherSchemes() {
        assertNull(DeepLinks.profileIdFromUri(Uri.parse("https://profile/abc")))
        assertNull(DeepLinks.profileIdFromUri(Uri.parse("qraft://gallery/abc")))
        assertNull(DeepLinks.profileIdFromUri(Uri.parse("qraft://profile/")))
    }

    @Test
    fun prefersExtraThenUri() {
        val intent = Intent().apply {
            putExtra(DeepLinks.EXTRA_PROFILE_ID, "from-extra")
            data = Uri.parse("qraft://profile/from-uri")
        }
        assertEquals("from-extra", DeepLinks.profileId(intent))
        assertEquals("from-uri", DeepLinks.profileId(Intent().setData(Uri.parse("qraft://profile/from-uri"))))
    }
}
