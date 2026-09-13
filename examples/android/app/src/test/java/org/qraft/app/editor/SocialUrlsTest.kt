package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Test

class SocialUrlsTest {
    @Test
    fun expandsHandles() {
        assertEquals(
            "https://instagram.com/qraft",
            SocialUrls.expand(SocialUrls.Platform.Instagram, "@qraft"),
        )
        assertEquals(
            "https://facebook.com/qraft",
            SocialUrls.expand(SocialUrls.Platform.Facebook, "qraft"),
        )
        assertEquals(
            "https://youtube.com/@qraft",
            SocialUrls.expand(SocialUrls.Platform.YouTube, "qraft"),
        )
    }

    @Test
    fun keepsFullUrls() {
        val url = "https://instagram.com/already"
        assertEquals(url, SocialUrls.expand(SocialUrls.Platform.Instagram, url))
    }

    @Test
    fun detectsPlatform() {
        assertEquals(SocialUrls.Platform.YouTube, SocialUrls.detect("https://youtu.be/x"))
        assertEquals(SocialUrls.Platform.Other, SocialUrls.detect("https://example.com"))
    }
}
