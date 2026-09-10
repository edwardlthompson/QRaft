package org.qraft.widget

import android.content.Context
import android.content.Intent
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile

object BrightenIntents {
    fun activityIntent(context: Context, profile: QrProfile?): Intent {
        val card = profile ?: DataStoreProfileRepository.seedWebsite()
        return Intent(context, BrightenActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(BrightenActivity.EXTRA_PAYLOAD, card.payloadText)
            .putExtra(BrightenActivity.EXTRA_STYLE, card.styleJson)
    }
}
