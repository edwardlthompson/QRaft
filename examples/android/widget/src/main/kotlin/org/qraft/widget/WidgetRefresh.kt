package org.qraft.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Drop cached bitmaps and refresh every QRaft glance widget after gallery edits. */
object WidgetRefresh {
    private val cache = QrWidgetCache()

    fun sharedCache(): QrWidgetCache = cache

    fun afterGalleryEdit(context: Context) {
        cache.clear()
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Default).launch {
            val manager = GlanceAppWidgetManager(appContext)
            manager.getGlanceIds(QrGlanceWidget::class.java).forEach { id ->
                QrGlanceWidget().update(appContext, id)
            }
        }
        // Also nudge classic AppWidgetManager in case Glance ids lag.
        val awm = AppWidgetManager.getInstance(appContext)
        val component = ComponentName(appContext, QrGlanceWidgetReceiver::class.java)
        val ids = awm.getAppWidgetIds(component)
        if (ids.isNotEmpty()) {
            awm.notifyAppWidgetViewDataChanged(ids, android.R.id.background)
        }
    }
}
