package org.qraft.widget

import android.content.Context
import org.qraft.data.DataStoreProfileRepository

/**
 * Per-appWidgetId preferences. Configure checkboxes never bleed across instances:
 * enabling carousel on widget A does not affect widget B.
 */
object WidgetPrefs {
    private const val PREFS = "qraft_widget"
    private const val KEY = "selected_id"
    private const val KEY_PENDING = "pending_id"

    fun selectedId(context: Context, widgetId: Int = 0): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (widgetId > 0) {
            prefs.getString(keyFor(widgetId), null)?.let { return it }
        }
        return prefs.getString(KEY, DataStoreProfileRepository.SEED_ID)
            ?: DataStoreProfileRepository.SEED_ID
    }

    fun setSelectedId(context: Context, id: String, widgetId: Int = 0) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        if (widgetId > 0) {
            editor.putString(keyFor(widgetId), id)
        } else {
            editor.putString(KEY, id)
        }
        editor.apply()
    }

    fun pendingId(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PENDING, null)

    fun setPendingId(context: Context, id: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PENDING, id).apply()
    }

    fun clearPendingId(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_PENDING).apply()
    }

    fun hasBoundId(context: Context, widgetId: Int): Boolean {
        if (widgetId <= 0) return false
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).contains(keyFor(widgetId))
    }

    /** Apply gallery pin target when the launcher skips (or has not yet run) configure. */
    fun consumePendingIfNeeded(context: Context, widgetId: Int) {
        if (widgetId <= 0) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val pending = prefs.getString(KEY_PENDING, null) ?: return
        if (prefs.contains(keyFor(widgetId))) return
        setSelectedId(context, pending, widgetId)
        prefs.edit().remove(KEY_PENDING).apply()
    }

    fun captionEnabled(context: Context, widgetId: Int = 0): Boolean {
        if (widgetId <= 0) return true
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(captionKey(widgetId))) return true
        return prefs.getBoolean(captionKey(widgetId), true)
    }

    fun setCaptionEnabled(context: Context, on: Boolean, widgetId: Int = 0) {
        if (widgetId <= 0) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(captionKey(widgetId), on)
            .apply()
    }

    /** Opt-in gallery cycle chrome (prev/next). Default off; never inherits from other widgets. */
    fun carouselEnabled(context: Context, widgetId: Int = 0): Boolean {
        if (widgetId <= 0) return false
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(carouselKey(widgetId))) return false
        return prefs.getBoolean(carouselKey(widgetId), false)
    }

    fun setCarouselEnabled(context: Context, on: Boolean, widgetId: Int = 0) {
        if (widgetId <= 0) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(carouselKey(widgetId), on)
            .apply()
    }

    fun sensitiveLock(context: Context, widgetId: Int = 0): Boolean {
        if (widgetId <= 0) return true
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(sensitiveKey(widgetId))) return true
        return prefs.getBoolean(sensitiveKey(widgetId), true)
    }

    fun setSensitiveLock(context: Context, on: Boolean, widgetId: Int = 0) {
        if (widgetId <= 0) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(sensitiveKey(widgetId), on)
            .apply()
    }

    fun transparentBg(context: Context, widgetId: Int = 0): Boolean {
        if (widgetId <= 0) return false
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(transparentKey(widgetId))) return false
        return prefs.getBoolean(transparentKey(widgetId), false)
    }

    fun setTransparentBg(context: Context, on: Boolean, widgetId: Int = 0) {
        if (widgetId <= 0) return
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(transparentKey(widgetId), on)
            .apply()
    }

    /** Carousel step; wraps first↔last. Positive = next, negative = previous. */
    fun cycle(context: Context, ids: List<String>, widgetId: Int = 0, delta: Int = 1) {
        if (ids.isEmpty() || delta == 0) return
        val cur = selectedId(context, widgetId)
        val at = ids.indexOf(cur).let { if (it < 0) 0 else it }
        val size = ids.size
        val index = ((at + delta) % size + size) % size
        setSelectedId(context, ids[index], widgetId)
    }

    private fun keyFor(widgetId: Int) = "selected_id_$widgetId"
    private fun captionKey(widgetId: Int) = "caption_$widgetId"
    private fun carouselKey(widgetId: Int) = "carousel_$widgetId"
    private fun sensitiveKey(widgetId: Int) = "sensitive_$widgetId"
    private fun transparentKey(widgetId: Int) = "transparent_$widgetId"
}
