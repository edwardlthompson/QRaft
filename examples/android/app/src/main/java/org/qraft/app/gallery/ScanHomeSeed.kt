package org.qraft.app.gallery

import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.data.QrProfile
import org.qraft.render.QrStyleJson

/** Seeds Home the same way Gallery Edit on Home does (`ProfileApply.fromProfile`). */
object ScanHomeSeed {
    fun apply(payload: String): Triple<EditorDraft, String, String> {
        val name = GalleryScanClone.suggestedName(payload)
        val applied = ProfileApply.fromProfile(
            QrProfile(id = "scan-draft", name = name, payloadText = payload),
        )
        return Triple(applied.first, QrStyleJson.encode(applied.second), name)
    }
}
