package org.qraft.widget

object WidgetTalkback {
    fun label(profileName: String): String = profileName.trim().ifBlank { "QR" }

    fun wifiPassword(payload: String): String? {
        if (!payload.startsWith("WIFI:", ignoreCase = true)) return null
        val match = Regex("P:([^;]*)").find(payload) ?: return null
        return match.groupValues[1].takeIf { it.isNotBlank() }
    }

    fun descriptionOmitsSecrets(description: String, payload: String): Boolean {
        val secret = wifiPassword(payload)
        return secret == null || !description.contains(secret)
    }
}
