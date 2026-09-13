package org.qraft.widget

/**
 * TalkBack labels for widget surfaces. Never include Wi-Fi passwords or other secrets.
 */
object WidgetTalkback {
    fun label(profileName: String): String = profileName.trim().ifBlank { "QR" }

    fun kindLabel(payload: String): String {
        val p = payload.trim()
        return when {
            p.startsWith("WIFI:", ignoreCase = true) -> "Wi-Fi"
            p.startsWith("BEGIN:VCARD", ignoreCase = true) -> "Contact"
            p.startsWith("BEGIN:VEVENT", ignoreCase = true) -> "Event"
            p.startsWith("GEO:", ignoreCase = true) -> "Location"
            p.startsWith("bitcoin:", ignoreCase = true) ||
                p.startsWith("ethereum:", ignoreCase = true) ||
                p.startsWith("litecoin:", ignoreCase = true) ||
                p.startsWith("dogecoin:", ignoreCase = true) ||
                p.startsWith("monero:", ignoreCase = true) -> "Crypto"
            p.startsWith("SMSTO:", ignoreCase = true) || p.startsWith("SMS:", ignoreCase = true) -> "SMS"
            p.startsWith("MATMSG:", ignoreCase = true) || p.startsWith("mailto:", ignoreCase = true) -> "Email"
            p.startsWith("tel:", ignoreCase = true) -> "Phone"
            p.startsWith("https://wa.me/", ignoreCase = true) -> "WhatsApp"
            p.startsWith("MECARD:", ignoreCase = true) -> "MeCard"
            p.startsWith("facetime", ignoreCase = true) -> "FaceTime"
            p.startsWith("http", ignoreCase = true) -> "Website"
            else -> "QR"
        }
    }

    fun wifiPassword(payload: String): String? {
        if (!payload.startsWith("WIFI:", ignoreCase = true)) return null
        val match = Regex("P:([^;]*)").find(payload) ?: return null
        return match.groupValues[1].takeIf { it.isNotBlank() }
    }

    fun secrets(payload: String): List<String> {
        val out = mutableListOf<String>()
        wifiPassword(payload)?.let { out.add(it) }
        // vCard NOTE lines can hold free text; never echo payload fragments into TalkBack.
        return out
    }

    fun descriptionOmitsSecrets(description: String, payload: String): Boolean {
        return secrets(payload).none { description.contains(it) }
    }

    fun talkbackName(profileName: String, payload: String): String {
        val base = label(profileName)
        val kind = kindLabel(payload)
        return if (base.equals(kind, ignoreCase = true)) base else "$base ($kind)"
    }
}
