package org.qraft.app.editor

import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

object ProfileApply {
    fun fromProfile(profile: QrProfile): Pair<EditorDraft, QrStyle> {
        val text = profile.payloadText
        val draft = when {
            text.startsWith("http", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Url, primary = text)
            text.startsWith("WIFI:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Wifi, primary = text)
            text.startsWith("BEGIN:VCARD", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.VCard, primary = text)
            text.startsWith("mailto:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Email, primary = text.removePrefix("mailto:"))
            text.startsWith("sms:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Sms, primary = text.removePrefix("sms:"))
            text.startsWith("tel:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Phone, primary = text.removePrefix("tel:"))
            else -> EditorDraft(kind = PayloadKind.Text, primary = text)
        }
        return draft to QrStyleJson.decode(profile.styleJson)
    }

    fun toProfile(
        id: String,
        name: String,
        draft: EditorDraft,
        style: QrStyle,
        sensitive: Boolean = false,
        now: Long,
        tags: List<String> = listOf("Website"),
    ): QrProfile? {
        val payload = draft.toPayload() ?: return null
        return QrProfile(
            id = id,
            name = name.trim().ifBlank { "Website" },
            payloadText = payload.encodeText(),
            tags = tags.ifEmpty { listOf("Website") },
            styleJson = QrStyleJson.encode(style),
            sensitive = sensitive,
            updatedAt = now,
        )
    }
}
