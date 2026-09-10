package org.qraft.data

import org.json.JSONArray
import org.json.JSONObject

object ProfileCodec {
    fun encodeList(profiles: List<QrProfile>): String {
        val arr = JSONArray()
        for (profile in profiles) {
            arr.put(
                JSONObject().apply {
                    put("id", profile.id)
                    put("name", profile.name)
                    put("payloadText", profile.payloadText)
                    put("tags", JSONArray(profile.tags))
                    put("styleJson", profile.styleJson)
                    put("sensitive", profile.sensitive)
                    put("updatedAt", profile.updatedAt)
                },
            )
        }
        return arr.toString()
    }

    fun decodeList(text: String): List<QrProfile> {
        if (text.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(text)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                val id = obj.optString("id")
                if (id.isBlank()) return@mapNotNull null
                val tagsJson = obj.optJSONArray("tags")
                val tags = if (tagsJson == null) {
                    emptyList()
                } else {
                    (0 until tagsJson.length()).map { tagsJson.optString(it) }
                }
                QrProfile(
                    id = id,
                    name = obj.optString("name"),
                    payloadText = obj.optString("payloadText"),
                    tags = tags,
                    styleJson = obj.optString("styleJson", "{}").ifBlank { "{}" },
                    sensitive = obj.optBoolean("sensitive", false),
                    updatedAt = obj.optLong("updatedAt", 0L),
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
