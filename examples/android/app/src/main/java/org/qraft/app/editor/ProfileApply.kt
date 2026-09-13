package org.qraft.app.editor

import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

object ProfileApply {
    fun fromProfile(profile: QrProfile): Pair<EditorDraft, QrStyle> {
        val text = profile.payloadText
        val draft = when {
            text.startsWith("MECARD:", ignoreCase = true) -> meCardDraft(text)
            text.startsWith("facetime-audio:", ignoreCase = true) ->
                EditorDraft(
                    kind = PayloadKind.FaceTime,
                    primary = text.substringAfter(':'),
                    secondary = "audio",
                )
            text.startsWith("facetime:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.FaceTime, primary = text.substringAfter(':'))
            text.startsWith("BEGIN:VEVENT", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Calendar, primary = text)
            text.startsWith("BEGIN:VCARD", ignoreCase = true) ->
                VCardDraft.fromPayloadText(text)
            text.startsWith("WIFI:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Wifi, primary = text)
            text.startsWith("GEO:", ignoreCase = true) || text.startsWith("geo:") -> {
                val coords = text.substringAfter(':').trim()
                val parts = coords.split(',', limit = 2)
                EditorDraft(
                    kind = PayloadKind.Geo,
                    primary = parts.getOrElse(0) { "" }.trim(),
                    secondary = parts.getOrElse(1) { "" }.trim(),
                )
            }
            text.startsWith("mailto:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Email, primary = text.removePrefix("mailto:"))
            text.startsWith("sms:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Sms, primary = text.removePrefix("sms:"))
            text.startsWith("tel:", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Phone, primary = text.removePrefix("tel:"))
            isWhatsApp(text) ->
                EditorDraft(kind = PayloadKind.WhatsApp, primary = text)
            isAppStore(text) ->
                EditorDraft(kind = PayloadKind.AppStore, primary = text)
            isSocial(text) ->
                EditorDraft(kind = PayloadKind.Social, primary = text)
            text.startsWith("http", ignoreCase = true) ->
                EditorDraft(kind = PayloadKind.Url, primary = text)
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

    private fun meCardDraft(text: String): EditorDraft {
        val body = text.removePrefix("MECARD:").removePrefix("mecard:")
        fun field(key: String): String =
            Regex("$key:([^;]*)", RegexOption.IGNORE_CASE).find(body)?.groupValues?.get(1)?.trim().orEmpty()
        return EditorDraft(
            kind = PayloadKind.MeCard,
            primary = field("N").ifBlank { body },
            secondary = field("TEL"),
            tertiary = field("EMAIL"),
        )
    }

    private fun isWhatsApp(text: String): Boolean =
        text.contains("wa.me/", ignoreCase = true)

    private fun isAppStore(text: String): Boolean {
        val t = text.lowercase()
        return t.contains("play.google.com") ||
            t.contains("f-droid.org") ||
            t.contains("apps.apple.com")
    }

    private fun isSocial(text: String): Boolean {
        val t = text.lowercase()
        return t.contains("matrix.to") ||
            t.contains("matrix.org") ||
            t.contains("mastodon.") ||
            Regex("""https?://[^/\s]+/@[\w.]""").containsMatchIn(text)
    }
}
