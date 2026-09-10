package org.qraft.app.shortcut

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import org.qraft.app.MainActivity
import org.qraft.data.QrProfile

object ProfileShortcuts {
    const val EXTRA_PROFILE_ID = "org.qraft.app.PROFILE_ID"

    fun publishOpen(context: Context, profile: QrProfile) {
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(EXTRA_PROFILE_ID, profile.id)
        }
        val info = ShortcutInfo.Builder(context, "profile-${profile.id}")
            .setShortLabel(profile.name.take(20).ifBlank { "QR" })
            .setIntent(intent)
            .build()
        runCatching { manager.addDynamicShortcuts(listOf(info)) }
    }
}
