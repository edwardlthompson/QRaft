package org.qraft.app.editor

/**
 * Parses stored vCard payload text back into editor fields.
 * Never put the raw `BEGIN:VCARD` blob into [EditorDraft.primary] (name).
 */
object VCardDraft {
    fun fromPayloadText(body: String): EditorDraft {
        val fn = field(body, "FN")
        val n = field(body, "N")
        val (given, family) = splitNames(fn, n)
        return EditorDraft(
            kind = PayloadKind.VCard,
            primary = given.ifBlank { EditorDraft.DEFAULT_GIVEN },
            givenName = given,
            familyName = family,
            secondary = field(body, "TEL"),
            tertiary = field(body, "EMAIL"),
            org = field(body, "ORG"),
            url = field(body, "URL"),
        )
    }

    fun splitNames(fn: String, n: String): Pair<String, String> {
        val nParts = n.split(';').map { it.trim() }
        var family = nParts.getOrElse(0) { "" }
        var given = nParts.getOrElse(1) { "" }
        val additional = nParts.getOrElse(2) { "" }
        if (given.isNotEmpty() && additional.isNotEmpty()) {
            given = "$given $additional".trim()
        }
        if (given.isEmpty() && family.isEmpty()) {
            val cleanedFn = fn.trim()
            if (cleanedFn.isNotEmpty() && !looksLikeVcardBlob(cleanedFn)) {
                val parts = cleanedFn.split(Regex("\\s+")).filter { it.isNotEmpty() }
                given = parts.firstOrNull().orEmpty()
                family = parts.drop(1).joinToString(" ")
            } else if (looksLikeVcardBlob(cleanedFn)) {
                return splitNames(field(cleanedFn, "FN"), field(cleanedFn, "N"))
            }
        }
        if (looksLikeVcardBlob(given) || looksLikeVcardBlob(fn)) {
            if (family.isNotEmpty() || (given.isNotEmpty() && !looksLikeVcardBlob(given))) {
                return given.takeUnless { looksLikeVcardBlob(it) }.orEmpty() to family
            }
        }
        return given to family
    }

    fun looksLikeVcardBlob(value: String): Boolean =
        value.trimStart().startsWith("BEGIN:VCARD", ignoreCase = true)

    fun field(body: String, name: String): String {
        val prefix = "$name:"
        return body.lineSequence()
            .map { it.trim() }
            .filter { it.startsWith(prefix, ignoreCase = true) }
            .map { it.substringAfter(':').trim() }
            .lastOrNull { it.isNotEmpty() && (name != "FN" || !looksLikeVcardBlob(it)) }
            ?: body.lineSequence()
                .map { it.trim() }
                .firstOrNull { it.startsWith(prefix, ignoreCase = true) }
                ?.substringAfter(':')
                ?.trim()
                .orEmpty()
    }
}
