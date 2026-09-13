package org.qraft.app.editor

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.qraft.render.CenterMark

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class CenterMarkIconsTest {
    @Test
    fun paintsRequestedSize() {
        val wifi = CenterMarkIcons.bitmap(CenterMark.WIFI, 64)
        val link = CenterMarkIcons.bitmap(CenterMark.LINK, 64)
        assertTrue(wifi.second == 64 && wifi.third == 64)
        assertTrue(wifi.first.size == 64 * 64)
        assertTrue(link.first.size == 64 * 64)
    }
}
