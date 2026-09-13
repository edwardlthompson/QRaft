package org.qraft.app.gallery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlinx.coroutines.runBlocking
import org.qraft.data.DataStoreProfileRepository

class GalleryScanCloneNameTest {
    @Test
    fun namesFromCommonPayloads() {
        assertEquals(
            "Edward Thompson",
            GalleryScanClone.suggestedName(
                "BEGIN:VCARD\nVERSION:3.0\nN:Thompson;Edward;;;\nFN:Edward Thompson\nEND:VCARD",
            ),
        )
        assertEquals("Cafe", GalleryScanClone.suggestedName("WIFI:T:WPA;S:Cafe;P:x;;"))
        assertTrue(GalleryScanClone.suggestedName("https://edwardlthompson.com/path").contains("edwardlthompson"))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class GalleryScanCloneSaveTest {
    @Test
    fun saveKeepsExactPayload() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val repo = DataStoreProfileRepository(context)
        val payload = "https://example.com/clone-me"
        val saved = GalleryScanClone.save(context, repo, payload, now = 42L)!!
        assertEquals(payload, saved.payloadText)
        assertEquals(listOf("Scanned"), saved.tags)
        assertEquals(payload, repo.get(saved.id)?.payloadText)
    }
}
