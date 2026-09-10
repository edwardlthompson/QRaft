package org.qraft.app.wifi

import android.content.Context
import android.net.wifi.WifiManager

object CurrentWifi {
    fun ssidOrNull(context: Context): String? {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null
        @Suppress("DEPRECATION")
        val raw = wm.connectionInfo?.ssid?.trim().orEmpty()
        val ssid = raw.removeSurrounding("\"")
        if (ssid.isBlank() || ssid.equals("<unknown ssid>", ignoreCase = true)) return null
        return ssid
    }
}
