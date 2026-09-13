package org.qraft.app.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Reads the connected Wi-Fi SSID. On Android 10+ this needs a runtime location
 * grant; missing permission or unknown SSID fails soft with null.
 */
object CurrentWifi {
    val locationPermission: String =
        if (Build.VERSION.SDK_INT >= 29) Manifest.permission.ACCESS_FINE_LOCATION
        else Manifest.permission.ACCESS_COARSE_LOCATION

    fun hasLocationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, locationPermission) ==
            PackageManager.PERMISSION_GRANTED

    fun needsLocationPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT >= 29 && !hasLocationPermission(context)

    fun ssidOrNull(context: Context): String? {
        if (needsLocationPermission(context)) return null
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null
        @Suppress("DEPRECATION")
        val raw = wm.connectionInfo?.ssid?.trim().orEmpty()
        val ssid = raw.removeSurrounding("\"")
        if (ssid.isBlank() || ssid.equals("<unknown ssid>", ignoreCase = true)) return null
        return ssid
    }
}
