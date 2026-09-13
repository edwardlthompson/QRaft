package org.qraft.app.scan

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ScanHistoryStoreTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun pushCapsAndDedupes() {
        ScanHistoryStore.clear(context)
        repeat(60) { i ->
            ScanHistoryStore.push(context, "payload-$i", "QR_CODE", at = i.toLong())
        }
        val all = ScanHistoryStore.all(context)
        assertEquals(ScanHistoryStore.MAX, all.size)
        assertEquals("payload-59", all.first().text)
        ScanHistoryStore.push(context, "payload-59", "QR_CODE", at = 100)
        assertEquals(1, ScanHistoryStore.all(context).count { it.text == "payload-59" })
        assertTrue(ScanHistoryStore.all(context).first().at >= 100)
    }
}
