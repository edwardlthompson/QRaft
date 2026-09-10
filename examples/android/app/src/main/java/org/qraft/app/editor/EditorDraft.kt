package org.qraft.app.editor

import org.qraft.coreqr.QrPayload
import org.qraft.coreqr.WifiSecurity

enum class PayloadKind { Url, Text, Wifi, VCard, Email, Sms, Phone, Crypto }

data class EditorDraft(
    val kind: PayloadKind = PayloadKind.Url,
    val primary: String = DEFAULT_URL,
    val secondary: String = "",
    val tertiary: String = "",
    val wifiSecurity: WifiSecurity = WifiSecurity.WPA,
    val wifiHidden: Boolean = false,
    val org: String = "",
    val url: String = "",
) {
    fun toPayload(): QrPayload? {
        val a = primary.trim()
        val b = secondary.trim()
        val c = tertiary.trim()
        return when (kind) {
            PayloadKind.Url -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Url(it) }
            PayloadKind.Text -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Text(it) }
            PayloadKind.Wifi -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.Wifi(it, b, wifiSecurity, wifiHidden)
            }
            PayloadKind.VCard -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.VCard(it, b, c, org.trim(), url.trim())
            }
            PayloadKind.Email -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.Email(it, b, c)
            }
            PayloadKind.Sms -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Sms(it, b) }
            PayloadKind.Phone -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Phone(it) }
            PayloadKind.Crypto -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.CryptoAddress(it, b)
            }
        }
    }

    companion object {
        const val DEFAULT_URL = "https://example.com"
        const val DEFAULT_TEXT = "Hello from QRaft"
        const val DEFAULT_WIFI = "QRaft"
        const val DEFAULT_VCARD = "Ada Lovelace"
        const val DEFAULT_EMAIL = "hello@example.com"
        const val DEFAULT_PHONE = "+15555550100"

        fun defaultPrimary(kind: PayloadKind): String = when (kind) {
            PayloadKind.Url -> DEFAULT_URL
            PayloadKind.Text -> DEFAULT_TEXT
            PayloadKind.Wifi -> DEFAULT_WIFI
            PayloadKind.VCard -> DEFAULT_VCARD
            PayloadKind.Email -> DEFAULT_EMAIL
            PayloadKind.Sms, PayloadKind.Phone -> DEFAULT_PHONE
            PayloadKind.Crypto -> ""
        }

        fun fromParts(
            kind: String,
            primary: String,
            secondary: String,
            tertiary: String,
            wifi: String,
            @Suppress("UNUSED_PARAMETER") ecc: String = "M",
            wifiHidden: Boolean = false,
            org: String = "",
            url: String = "",
        ): EditorDraft = EditorDraft(
            kind = runCatching { PayloadKind.valueOf(kind) }.getOrDefault(PayloadKind.Url),
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            wifiSecurity = runCatching { WifiSecurity.valueOf(wifi) }.getOrDefault(WifiSecurity.WPA),
            wifiHidden = wifiHidden,
            org = org,
            url = url,
        )
    }
}
