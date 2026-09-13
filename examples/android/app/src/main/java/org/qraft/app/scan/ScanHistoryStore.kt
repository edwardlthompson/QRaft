package org.qraft.app.scan

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ScanHistoryEntry(
    val text: String,
    val format: String,
    val at: Long,
)

/** Local capped scan history (offline SharedPreferences). */
object ScanHistoryStore {
    const val MAX = 50
    private const val PREFS = "qraft_scan_history"
    private const val KEY = "entries"

    fun all(context: Context): List<ScanHistoryEntry> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
            ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    add(
                        ScanHistoryEntry(
                            text = o.getString("text"),
                            format = o.optString("format", "QR_CODE"),
                            at = o.optLong("at", 0L),
                        ),
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun push(context: Context, text: String, format: String, at: Long = System.currentTimeMillis()) {
        if (text.isBlank()) return
        val next = listOf(ScanHistoryEntry(text, format, at)) +
            all(context).filterNot { it.text == text && it.format == format }
        save(context, next.take(MAX))
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY).apply()
    }

    private fun save(context: Context, entries: List<ScanHistoryEntry>) {
        val arr = JSONArray()
        entries.forEach { e ->
            arr.put(
                JSONObject()
                    .put("text", e.text)
                    .put("format", e.format)
                    .put("at", e.at),
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, arr.toString())
            .apply()
    }
}
