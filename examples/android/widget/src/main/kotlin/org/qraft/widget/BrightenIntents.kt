package org.qraft.widget

import android.content.Context
import android.content.Intent
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile

object BrightenIntents {
    fun activityIntent(
        context: Context,
        profile: QrProfile?,
        lockEnabled: Boolean = WidgetPrefs.sensitiveLock(context),
    ): Intent {
        val card = profile ?: DataStoreProfileRepository.seedWebsite()
        val intent = Intent(context, BrightenActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return if (SensitiveUnlock.requiresAuth(card.sensitive, lockEnabled)) {
            intent
                .putExtra(BrightenActivity.EXTRA_PROFILE_ID, card.id)
                .putExtra(BrightenActivity.EXTRA_REQUIRES_AUTH, true)
        } else {
            intent
                .putExtra(BrightenActivity.EXTRA_PAYLOAD, card.payloadText)
                .putExtra(BrightenActivity.EXTRA_STYLE, card.styleJson)
                .putExtra(BrightenActivity.EXTRA_CAPTION, WidgetCaption.forBitmap(card, nameFallback = true))
        }
    }
}
