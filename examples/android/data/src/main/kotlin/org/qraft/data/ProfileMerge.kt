package org.qraft.data

import java.io.File
import org.json.JSONObject

object ProfileMerge {
    private val PATH_KEYS = arrayOf("imageBackgroundPath", "logoImagePath")

    fun apply(current: Map<String, QrProfile>, incoming: List<QrProfile>): Map<String, QrProfile> {
        val next = LinkedHashMap(current)
        incoming.forEach { inc ->
            val local = next[inc.id]
            if (local == null || inc.updatedAt > local.updatedAt) {
                next[inc.id] = sanitize(inc)
            }
        }
        return next
    }

    fun wonIds(current: Map<String, QrProfile>, incoming: List<QrProfile>): Set<String> =
        incoming.mapNotNull { inc ->
            val local = current[inc.id]
            if (local == null || inc.updatedAt > local.updatedAt) inc.id else null
        }.toSet()

    fun sanitize(profile: QrProfile): QrProfile {
        val raw = profile.styleJson
        if (raw.isBlank() || raw == "{}") return profile
        return runCatching {
            val obj = JSONObject(raw)
            var changed = false
            for (key in PATH_KEYS) {
                val path = obj.optString(key, "")
                if (path.isNotBlank() && !File(path).isFile) {
                    obj.put(key, "")
                    changed = true
                }
            }
            if (changed) profile.copy(styleJson = obj.toString()) else profile
        }.getOrDefault(profile)
    }
}
