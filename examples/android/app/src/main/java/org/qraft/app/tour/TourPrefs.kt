package org.qraft.app.tour

import android.content.Context

/** First-run Home → Gallery → Widget coach marks. Default: show once. */
class TourPrefs(private val context: Context) {
    fun completed(): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY, false)

    fun setCompleted(done: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY, done).apply()
    }

    companion object {
        const val PREFS = "qraft_tour"
        const val KEY = "completed"
    }
}
