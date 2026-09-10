package org.qraft.app.editor

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.render.QrStyleJson
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DraftStoreTest {
    @Test
    fun roundTripSurvivesReload() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val draft = EditorDraft(kind = PayloadKind.Wifi, primary = "Cafe", wifiHidden = true)
        DraftStore.save(context, draft, QrStyleJson.encode(org.qraft.render.QrStyle.DEFAULT), "Wifi")
        assertEquals(true, DraftStore.loadDraft(context)?.wifiHidden)
        assertEquals(PayloadKind.Wifi, DraftStore.loadDraft(context)?.kind)
        assertEquals("Wifi", DraftStore.loadName(context))
    }
}
