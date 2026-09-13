package org.qraft.coreqr

/**
 * vCard name helpers. iOS Contacts fills First/Last from structured [N], not [FN] alone;
 * Android often accepts [FN]-only. Emit both for cross-platform scans.
 */
object VCardNames {
    /** RFC 6350 / vCard 3.0 `N:` value: Family;Given;Additional;Prefix;Suffix. */
    fun structuredN(givenName: String, familyName: String): String {
        val given = escape(givenName.trim())
        val family = escape(familyName.trim())
        return "$family;$given;;;"
    }

    fun displayName(givenName: String, familyName: String): String =
        listOf(givenName.trim(), familyName.trim()).filter { it.isNotEmpty() }.joinToString(" ")

    fun encode(
        givenName: String,
        familyName: String = "",
        phone: String = "",
        email: String = "",
        org: String = "",
        url: String = "",
    ): String {
        val fn = displayName(givenName, familyName)
        return buildString {
            append("BEGIN:VCARD\nVERSION:3.0\n")
            append("N:").append(structuredN(givenName, familyName)).append('\n')
            if (fn.isNotEmpty()) append("FN:").append(fn).append('\n')
            if (org.isNotBlank()) append("ORG:").append(org).append('\n')
            if (phone.isNotBlank()) append("TEL:").append(phone).append('\n')
            if (email.isNotBlank()) append("EMAIL:").append(email).append('\n')
            if (url.isNotBlank()) append("URL:").append(url).append('\n')
            append("END:VCARD")
        }
    }

    private fun escape(value: String): String =
        value.replace("\\", "\\\\").replace(";", "\\;").replace(",", "\\,")
}
