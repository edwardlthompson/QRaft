package org.qraft.app.ui.wall

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind

class WallpaperEmptyTest {
    @Test
    fun emptyUrlCannotSet() {
        assertFalse(WallpaperEmpty.canSet(EditorDraft(kind = PayloadKind.Url, primary = "")))
        assertTrue(WallpaperEmpty.canSet(EditorDraft()))
    }
}
