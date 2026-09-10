package org.qraft.widget

import android.service.quicksettings.TileService
import kotlinx.coroutines.runBlocking
import org.qraft.data.DataStoreProfileRepository

class BrightenTileService : TileService() {
    override fun onClick() {
        val repo = DataStoreProfileRepository(this)
        val profile = runBlocking { repo.get(WidgetPrefs.selectedId(this@BrightenTileService)) }
        @Suppress("DEPRECATION")
        startActivityAndCollapse(BrightenIntents.activityIntent(this, profile))
    }
}
