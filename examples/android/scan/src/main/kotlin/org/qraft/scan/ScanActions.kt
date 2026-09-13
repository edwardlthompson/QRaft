package org.qraft.scan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings

enum class ScanActionKind { Open, Copy, JoinWifi, None }

object ScanActions {
    fun kind(payload: String): ScanActionKind {
        val t = payload.trim()
        if (WifiScanParse.parse(t) != null) return ScanActionKind.JoinWifi
        if (canOpen(t)) return ScanActionKind.Open
        return if (t.isNotEmpty()) ScanActionKind.Copy else ScanActionKind.None
    }

    fun canOpen(payload: String): Boolean {
        val t = payload.trim().lowercase()
        return t.startsWith("http://") || t.startsWith("https://") ||
            t.startsWith("mailto:") || t.startsWith("tel:") ||
            t.startsWith("sms:") || t.startsWith("geo:") ||
            t.startsWith("facetime:") || t.startsWith("facetime-audio:")
    }

    fun copy(context: Context, payload: String) {
        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip.setPrimaryClip(ClipData.newPlainText("qr", payload))
    }

    fun open(context: Context, payload: String): Boolean {
        if (!canOpen(payload)) return false
        return runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(payload.trim())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            true
        }.getOrDefault(false)
    }

    /** Suggest Wi-Fi (API 29+) or open system Wi-Fi settings after copying the password. */
    fun joinWifi(context: Context, payload: String): Boolean {
        val wifi = WifiScanParse.parse(payload) ?: return false
        copy(context, wifi.password.ifEmpty { wifi.ssid })
        if (Build.VERSION.SDK_INT >= 29 && wifi.ssid.isNotEmpty()) {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(wifi.ssid)
                .apply {
                    if (wifi.password.isNotEmpty() &&
                        !wifi.security.equals("nopass", ignoreCase = true)
                    ) {
                        setWpa2Passphrase(wifi.password)
                    }
                    setIsHiddenSsid(wifi.hidden)
                }
                .build()
            runCatching {
                @Suppress("DEPRECATION")
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE)
                    as android.net.wifi.WifiManager
                wm.addNetworkSuggestions(listOf(suggestion))
            }
        }
        return runCatching {
            context.startActivity(
                Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            true
        }.getOrDefault(false)
    }
}
