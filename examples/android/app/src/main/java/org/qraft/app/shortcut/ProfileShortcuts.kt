package org.qraft.app.shortcut

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import org.qraft.app.MainActivity
import org.qraft.app.R
import org.qraft.app.deeplink.DeepLinks
import org.qraft.data.QrProfile

object ProfileShortcuts {
    const val EXTRA_PROFILE_ID = DeepLinks.EXTRA_PROFILE_ID
    const val EXTRA_NEW_QR = "org.qraft.app.NEW_QR"
    const val ACTION_NEW = "org.qraft.app.action.NEW_QR"
    const val LAST_ALIAS = "last"
    private const val ID_LAST = "last_card"
    private const val PREFS = "qraft_shortcuts"
    private const val KEY_LAST = "last_profile_id"

    fun profileIdFromIntent(intent: Intent?): String? {
        if (intent == null) return null
        if (intent.action == ACTION_NEW || intent.getBooleanExtra(EXTRA_NEW_QR, false)) return null
        val raw = DeepLinks.profileId(intent) ?: return null
        return raw.takeUnless { it.equals(LAST_ALIAS, ignoreCase = true) }
    }

    fun wantsNewQr(intent: Intent?): Boolean {
        if (intent == null) return false
        return intent.action == ACTION_NEW || intent.getBooleanExtra(EXTRA_NEW_QR, false)
    }

    fun wantsLastCard(intent: Intent?): Boolean {
        val id = DeepLinks.profileId(intent) ?: return false
        return id.equals(LAST_ALIAS, ignoreCase = true)
    }

    fun lastProfileId(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LAST, null)

    fun publishOpen(context: Context, profile: QrProfile) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST, profile.id)
            .apply()
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return
        val open = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = DeepLinks.profileUri(profile.id)
            putExtra(EXTRA_PROFILE_ID, profile.id)
        }
        val last = ShortcutInfo.Builder(context, ID_LAST)
            .setShortLabel(context.getString(R.string.shortcut_last_card))
            .setLongLabel(context.getString(R.string.shortcut_last_card_long))
            .setIntent(open)
            .build()
        val dynamic = ShortcutInfo.Builder(context, "profile-${profile.id}")
            .setShortLabel(profile.name.take(20).ifBlank { "QR" })
            .setIntent(open)
            .build()
        runCatching {
            manager.addDynamicShortcuts(listOf(dynamic))
            manager.updateShortcuts(listOf(last))
        }
    }
}
