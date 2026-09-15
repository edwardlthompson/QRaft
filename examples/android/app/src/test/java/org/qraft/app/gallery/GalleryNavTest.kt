package org.qraft.app.gallery

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GalleryNavTest {
    @Test
    fun consumeBackNullVsId() {
        assertFalse(GalleryNav.consumeBack(null))
        assertFalse(GalleryNav.consumeBack(""))
        assertTrue(GalleryNav.consumeBack("card-1"))
    }
}
