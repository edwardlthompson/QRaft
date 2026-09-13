package org.qraft.widget

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.runBlocking
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile

/** Quick Settings tile → full-screen bright QR for the selected gallery card. */
object BrightenTile {
    const val REQUEST_CODE = 43

    fun pendingIntent(context: Context, profile: QrProfile?): PendingIntent {
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            BrightenIntents.activityIntent(context, profile),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

class BrightenTileService : TileService() {
    override fun onStartListening() {
        qsTile?.apply {
            state = Tile.STATE_ACTIVE
            label = getString(R.string.widget_tile_label)
            updateTile()
        }
    }

    override fun onClick() {
        val repo = DataStoreProfileRepository(this)
        val profile = runBlocking { repo.get(WidgetPrefs.selectedId(this@BrightenTileService)) }
        val intent = BrightenIntents.activityIntent(this, profile)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(BrightenTile.pendingIntent(this, profile))
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
