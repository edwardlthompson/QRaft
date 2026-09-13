package org.qraft.app.editor

import org.qraft.coreqr.QrPayload
import org.qraft.coreqr.WifiSecurity

enum class PayloadKind {
    Url, Text, Wifi, VCard, Email, Sms, Phone, Crypto,
    Calendar, Geo, WhatsApp, AppStore, Social, MeCard, FaceTime, Barcode,
}

data class EditorDraft(
    val kind: PayloadKind = PayloadKind.Url,
    val primary: String = DEFAULT_URL,
    val secondary: String = "",
    val tertiary: String = "",
    val wifiSecurity: WifiSecurity = WifiSecurity.WPA,
    val wifiHidden: Boolean = false,
    val org: String = "",
    val url: String = "",
    val givenName: String = "",
    val familyName: String = "",
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
            PayloadKind.VCard -> vcardPayload(b, c)
            PayloadKind.Email -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.Email(it, b, c)
            }
            PayloadKind.Sms -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Sms(it, b) }
            PayloadKind.Phone -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Phone(it) }
            PayloadKind.Crypto -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.CryptoAddress(it, b)
            }
            PayloadKind.Calendar -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.CalendarEvent(it, b, c, org.trim())
            }
            PayloadKind.Geo -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.Geo(it, b.ifEmpty { DEFAULT_LON })
            }
            PayloadKind.WhatsApp -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.WhatsApp(it, b) }
            PayloadKind.AppStore -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.AppStore(it) }
            PayloadKind.Social -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.SocialUrl(it) }
            PayloadKind.MeCard -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.MeCard(it, b, c) }
            PayloadKind.FaceTime -> a.takeIf { it.isNotEmpty() }?.let {
                QrPayload.FaceTime(it, audio = b.equals("audio", ignoreCase = true))
            }
            // Stored as text so gallery can keep the digits; preview uses BarcodeEncoder.
            PayloadKind.Barcode -> a.takeIf { it.isNotEmpty() }?.let { QrPayload.Text(it) }
        }
    }

    private fun vcardPayload(phone: String, email: String): QrPayload.VCard? {
        var given = givenName.trim()
        var family = familyName.trim()
        if (given.isEmpty() && family.isEmpty() && primary.isNotBlank()) {
            if (VCardDraft.looksLikeVcardBlob(primary)) {
                val parsed = VCardDraft.fromPayloadText(primary)
                given = parsed.givenName
                family = parsed.familyName
            } else {
                val parts = primary.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                given = parts.firstOrNull().orEmpty()
                family = parts.drop(1).joinToString(" ")
            }
        }
        if (given.isEmpty() && family.isEmpty()) return null
        return QrPayload.VCard(given, family, phone, email, org.trim(), url.trim())
    }

    companion object {
        const val DEFAULT_URL = "https://example.com"
        const val DEFAULT_TEXT = "Hello from QRaft"
        const val DEFAULT_WIFI = "QRaft"
        const val DEFAULT_VCARD = "Ada Lovelace"
        const val DEFAULT_GIVEN = "Ada"
        const val DEFAULT_FAMILY = "Lovelace"
        const val DEFAULT_EMAIL = "hello@example.com"
        const val DEFAULT_PHONE = "+15555550100"
        const val DEFAULT_CALENDAR = "Team meeting"
        const val DEFAULT_LAT = "37.7749"
        const val DEFAULT_LON = "-122.4194"
        const val DEFAULT_APPSTORE = "https://f-droid.org/packages/org.qraft.app/"
        const val DEFAULT_SOCIAL = "https://matrix.to/#/"

        fun defaultPrimary(kind: PayloadKind): String = when (kind) {
            PayloadKind.Url -> DEFAULT_URL
            PayloadKind.Text -> DEFAULT_TEXT
            PayloadKind.Wifi -> DEFAULT_WIFI
            PayloadKind.VCard -> DEFAULT_GIVEN
            PayloadKind.Email -> DEFAULT_EMAIL
            PayloadKind.Sms, PayloadKind.Phone, PayloadKind.WhatsApp -> DEFAULT_PHONE
            PayloadKind.Crypto -> ""
            PayloadKind.Calendar -> DEFAULT_CALENDAR
            PayloadKind.Geo -> DEFAULT_LAT
            PayloadKind.AppStore -> DEFAULT_APPSTORE
            PayloadKind.Social -> DEFAULT_SOCIAL
            PayloadKind.MeCard -> DEFAULT_VCARD
            PayloadKind.FaceTime -> DEFAULT_EMAIL
            PayloadKind.Barcode -> "123456789012"
        }

        fun withKind(kind: PayloadKind): EditorDraft = EditorDraft(
            kind = kind,
            primary = defaultPrimary(kind),
            secondary = when (kind) {
                PayloadKind.Barcode -> "Code128"
                PayloadKind.Social -> "Other"
                else -> ""
            },
            givenName = if (kind == PayloadKind.VCard) DEFAULT_GIVEN else "",
            familyName = if (kind == PayloadKind.VCard) DEFAULT_FAMILY else "",
        )

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
            givenName: String = "",
            familyName: String = "",
        ): EditorDraft = EditorDraft(
            kind = runCatching { PayloadKind.valueOf(kind) }.getOrDefault(PayloadKind.Url),
            primary = primary,
            secondary = secondary,
            tertiary = tertiary,
            wifiSecurity = runCatching { WifiSecurity.valueOf(wifi) }.getOrDefault(WifiSecurity.WPA),
            wifiHidden = wifiHidden,
            org = org,
            url = url,
            givenName = givenName,
            familyName = familyName,
        )
    }
}
