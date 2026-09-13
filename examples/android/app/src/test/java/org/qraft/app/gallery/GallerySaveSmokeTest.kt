package org.qraft.app.gallery

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.data.DataStoreProfileRepository
import org.qraft.render.QrStyle
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class GallerySaveSmokeTest {
    @Test
    fun makeQrAndSaveToGalleryWritesThumb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repo = DataStoreProfileRepository(context)
        val draft = EditorDraft(kind = PayloadKind.Url, primary = "https://f-droid.org/smoke")
        val profile = GallerySave.write(context, repo, "Smoke card", draft, QrStyle.DEFAULT, 1L)
        assertNotNull(profile)
        assertEquals("Smoke card", profile!!.name)
        assertTrue(GalleryStore.thumbFile(context, profile.id).exists())
        assertTrue(repo.all().any { it.id == profile.id })
    }

    @Test
    fun updateExistingGalleryCardKeepsId() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repo = DataStoreProfileRepository(context)
        val created = GallerySave.write(
            context,
            repo,
            "Edit me",
            EditorDraft(kind = PayloadKind.Url, primary = "https://example.com/a"),
            QrStyle.DEFAULT,
            1L,
        )
        assertNotNull(created)
        val updated = GallerySave.update(
            context = context,
            repo = repo,
            existing = created!!,
            name = "Edited",
            draft = EditorDraft(kind = PayloadKind.Url, primary = "https://example.com/b"),
            style = QrStyle.DEFAULT,
            now = 2L,
        )
        assertNotNull(updated)
        assertEquals(created.id, updated!!.id)
        assertEquals("Edited", updated.name)
        assertTrue(updated.payloadText.contains("example.com/b"))
        assertEquals(1, repo.all().count { it.id == created.id })
    }
}
