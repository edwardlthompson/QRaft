package org.qraft.app.share

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import org.qraft.widget.QrGlanceWidgetReceiver
import org.qraft.widget.WidgetPrefs

object WidgetPin {
    /** Pin a known gallery card (pre-selects it in the configure screen). */
    fun request(context: Context, profileId: String): Boolean {
        WidgetPrefs.setPendingId(context, profileId)
        return requestPin(context)
    }

    /** Pin without a pre-selected card — configure screen is a gallery picker. */
    fun requestPick(context: Context): Boolean {
        WidgetPrefs.clearPendingId(context)
        return requestPin(context)
    }

    private fun requestPin(context: Context): Boolean {
        val manager = AppWidgetManager.getInstance(context)
        if (Build.VERSION.SDK_INT < 26 || !manager.isRequestPinAppWidgetSupported) return false
        val provider = ComponentName(context, QrGlanceWidgetReceiver::class.java)
        return manager.requestPinAppWidget(provider, null, null as PendingIntent?)
    }
}
