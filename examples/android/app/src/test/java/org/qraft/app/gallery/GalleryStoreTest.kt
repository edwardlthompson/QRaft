package org.qraft.app.gallery

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class GalleryStoreTest {
    @Test
    fun writeAndDeleteSidecarDir() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val id = GalleryStore.newId()
        GalleryStore.writeThumb(context, id, byteArrayOf(1, 2, 3))
        assertTrue(GalleryStore.thumbFile(context, id).exists())
        GalleryStore.delete(context, id)
        assertFalse(GalleryStore.dir(context, id).exists())
    }
}
