package org.qraft.widget

import android.content.Context
import org.qraft.data.DataStoreProfileRepository

object WidgetPrefs {
    private const val PREFS = "qraft_widget"
    private const val KEY = "selected_id"
    private const val KEY_PENDING = "pending_id"
    private const val KEY_CAPTION = "caption"
    private const val KEY_SENSITIVE = "sensitive_lock"
    private const val KEY_TRANSPARENT = "transparent_bg"

    fun selectedId(context: Context, widgetId: Int = 0): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val per = if (widgetId > 0) prefs.getString(keyFor(widgetId), null) else null
        return per ?: prefs.getString(KEY, DataStoreProfileRepository.SEED_ID)
            ?: DataStoreProfileRepository.SEED_ID
    }

    fun setSelectedId(context: Context, id: String, widgetId: Int = 0) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        if (widgetId > 0) editor.putString(keyFor(widgetId), id)
        editor.putString(KEY, id)
        editor.apply()
    }

    fun pendingId(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PENDING, null)

    fun setPendingId(context: Context, id: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PENDING, id).apply()
    }

    fun captionEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_CAPTION, false)

    fun setCaptionEnabled(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_CAPTION, on).apply()
    }

    fun sensitiveLock(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_SENSITIVE, true)

    fun setSensitiveLock(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_SENSITIVE, on).apply()
    }

    fun transparentBg(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_TRANSPARENT, true)

    fun setTransparentBg(context: Context, on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_TRANSPARENT, on).apply()
    }

    fun cycle(context: Context, ids: List<String>, widgetId: Int = 0) {
        if (ids.isEmpty()) return
        val cur = selectedId(context, widgetId)
        val index = ids.indexOf(cur).let { if (it < 0) 0 else (it + 1) % ids.size }
        setSelectedId(context, ids[index], widgetId)
    }

    private fun keyFor(widgetId: Int) = "selected_id_$widgetId"
}
