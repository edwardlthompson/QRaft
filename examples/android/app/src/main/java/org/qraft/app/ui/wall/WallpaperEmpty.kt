package org.qraft.app.ui.wall

import org.qraft.app.editor.EditorDraft

object WallpaperEmpty {
    fun canSet(draft: EditorDraft): Boolean = draft.toPayload() != null
}
