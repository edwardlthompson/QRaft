package org.qraft.app.share

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import org.qraft.widget.QrGlanceWidgetReceiver
import org.qraft.widget.WidgetPrefs

object WidgetPin {
    fun request(context: Context, profileId: String): Boolean {
        WidgetPrefs.setPendingId(context, profileId)
        val manager = AppWidgetManager.getInstance(context)
        if (Build.VERSION.SDK_INT < 26 || !manager.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(context, QrGlanceWidgetReceiver::class.java)
        val success = manager.requestPinAppWidget(provider, null, null as PendingIntent?)
        return success
    }
}
