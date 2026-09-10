package org.qraft.app.share

import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind

object ShareIntake {
    const val MAX_CHARS = 4096

    fun parse(text: String?, mime: String = "text/plain"): EditorDraft? {
        val raw = text?.trim().orEmpty()
        if (raw.isEmpty()) return null
        val clipped = if (raw.length > MAX_CHARS) raw.take(MAX_CHARS) else raw
        val lower = mime.lowercase()
        if (lower.contains("vcard") || clipped.startsWith("BEGIN:VCARD", ignoreCase = true)) {
            return vcardDraft(clipped)
        }
        val line = clipped.lineSequence().firstOrNull().orEmpty()
        return when {
            line.startsWith("http://", true) || line.startsWith("https://", true) ->
                EditorDraft(kind = PayloadKind.Url, primary = line)
            line.startsWith("mailto:", true) ->
                EditorDraft(kind = PayloadKind.Email, primary = line.removePrefix("mailto:").substringBefore('?'))
            line.startsWith("tel:", true) ->
                EditorDraft(kind = PayloadKind.Phone, primary = line.removePrefix("tel:"))
            line.startsWith("sms:", true) ->
                EditorDraft(kind = PayloadKind.Sms, primary = line.removePrefix("sms:").substringBefore('?'))
            line.startsWith("WIFI:", true) ->
                EditorDraft(kind = PayloadKind.Wifi, primary = clipped)
            line.startsWith("geo:", true) ->
                EditorDraft(kind = PayloadKind.Text, primary = line)
            else -> EditorDraft(kind = PayloadKind.Text, primary = clipped)
        }
    }

    private fun vcardDraft(body: String): EditorDraft {
        fun field(name: String): String {
            val prefix = "$name:"
            return body.lineSequence()
                .firstOrNull { it.startsWith(prefix, ignoreCase = true) }
                ?.substringAfter(':')
                ?.trim()
                .orEmpty()
        }
        val name = field("FN").ifBlank { field("N").replace(";", " ").trim() }
        return EditorDraft(
            kind = PayloadKind.VCard,
            primary = name.ifBlank { "Contact" },
            secondary = field("TEL"),
            tertiary = field("EMAIL"),
        )
    }
}
