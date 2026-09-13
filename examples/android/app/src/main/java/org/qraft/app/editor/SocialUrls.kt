package org.qraft.app.editor

/** Offline social URL helpers (Instagram / Facebook / YouTube / generic). */
object SocialUrls {
    enum class Platform { Instagram, Facebook, YouTube, Mastodon, Other }

    fun expand(platform: Platform, handleOrUrl: String): String {
        val raw = handleOrUrl.trim()
        if (raw.isEmpty()) return raw
        if (raw.startsWith("http://", true) || raw.startsWith("https://", true)) return raw
        val handle = raw.removePrefix("@").trim()
        return when (platform) {
            Platform.Instagram -> "https://instagram.com/$handle"
            Platform.Facebook -> "https://facebook.com/$handle"
            Platform.YouTube -> "https://youtube.com/@$handle"
            Platform.Mastodon -> if (handle.contains('@')) "https://$handle" else "https://mastodon.social/@$handle"
            Platform.Other -> raw
        }
    }

    fun detect(url: String): Platform {
        val u = url.lowercase()
        return when {
            "instagram.com" in u -> Platform.Instagram
            "facebook.com" in u || "fb.me" in u -> Platform.Facebook
            "youtube.com" in u || "youtu.be" in u -> Platform.YouTube
            "mastodon" in u -> Platform.Mastodon
            else -> Platform.Other
        }
    }
}
