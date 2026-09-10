package org.qraft.app.settings

import android.content.Context

/** Launch donate/update dialogs. Default off so the QR editor is not blocked. */
class ProductPrefs(private val context: Context) {
    fun nudgePrompts(): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, DEFAULT)

    fun setNudgePrompts(on: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, on).apply()
    }

    companion object {
        const val PREFS = "qraft_product"
        const val KEY = "nudge_prompts"
        const val DEFAULT = false
    }
}
