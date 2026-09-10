package org.qraft.app.share

import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.data.QrProfile
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

object ExportIntake {
    fun documentOf(text: String?): QrExportDocument? {
        if (text.isNullOrBlank()) return null
        return QrExportJson.decode(text.trim())
    }

    fun apply(text: String?): Pair<EditorDraft, QrStyle>? {
        val doc = documentOf(text) ?: return null
        val profile = QrProfile(
            id = "import",
            name = "Imported",
            payloadText = doc.payloadText,
            styleJson = QrStyleJson.encode(doc.style),
        )
        return ProfileApply.fromProfile(profile)
    }
}
