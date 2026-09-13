package org.qraft.coreqr

/**
 * Typed payloads encoded into a QR matrix. Visual styling never lives here —
 * only the string/bytes that become modules.
 */
sealed class QrPayload {
    abstract fun encodeText(): String

    data class Url(val url: String) : QrPayload() {
        override fun encodeText(): String = url.trim()
    }

    data class Text(val text: String) : QrPayload() {
        override fun encodeText(): String = text
    }

    data class Wifi(
        val ssid: String,
        val password: String = "",
        val security: WifiSecurity = WifiSecurity.WPA,
        val hidden: Boolean = false,
    ) : QrPayload() {
        override fun encodeText(): String {
            val auth = when (security) {
                WifiSecurity.NOPASS -> "nopass"
                WifiSecurity.WEP -> "WEP"
                WifiSecurity.WPA -> "WPA"
            }
            val hide = if (hidden) "H:true;" else ""
            return "WIFI:T:$auth;S:${escapeWifi(ssid)};P:${escapeWifi(password)};$hide;"
        }
    }

    data class VCard(
        val givenName: String = "",
        val familyName: String = "",
        val phone: String = "",
        val email: String = "",
        val org: String = "",
        val url: String = "",
    ) : QrPayload() {
        val fullName: String get() = VCardNames.displayName(givenName, familyName)
        override fun encodeText(): String =
            VCardNames.encode(givenName, familyName, phone, email, org, url)
    }

    data class Email(val address: String, val subject: String = "", val body: String = "") : QrPayload() {
        override fun encodeText(): String {
            if (subject.isBlank() && body.isBlank()) return "mailto:${address.trim()}"
            val q = listOfNotNull(
                subject.takeIf { it.isNotBlank() }?.let { "subject=${encodeQuery(it)}" },
                body.takeIf { it.isNotBlank() }?.let { "body=${encodeQuery(it)}" },
            ).joinToString("&")
            return "mailto:${address.trim()}?$q"
        }
    }

    data class Sms(val number: String, val body: String = "") : QrPayload() {
        override fun encodeText(): String =
            if (body.isBlank()) "sms:${number.trim()}"
            else "sms:${number.trim()}?body=${encodeQuery(body)}"
    }

    data class Phone(val number: String) : QrPayload() {
        override fun encodeText(): String = "tel:${number.trim()}"
    }

    data class CryptoAddress(val address: String, val scheme: String = "") : QrPayload() {
        override fun encodeText(): String {
            val a = address.trim()
            return if (scheme.isBlank()) a else "${scheme.trim()}:$a"
        }
    }

    data class CalendarEvent(
        val summary: String,
        val dtStart: String = "",
        val location: String = "",
        val description: String = "",
    ) : QrPayload() {
        override fun encodeText(): String = buildString {
            append("BEGIN:VEVENT\n")
            append("SUMMARY:").append(summary).append('\n')
            if (dtStart.isNotBlank()) append("DTSTART:").append(dtStart).append('\n')
            if (location.isNotBlank()) append("LOCATION:").append(location).append('\n')
            if (description.isNotBlank()) append("DESCRIPTION:").append(description).append('\n')
            append("END:VEVENT")
        }
    }

    data class Geo(val latitude: String, val longitude: String) : QrPayload() {
        override fun encodeText(): String =
            "GEO:${latitude.trim()},${longitude.trim()}"
    }

    data class WhatsApp(val phone: String, val text: String = "") : QrPayload() {
        override fun encodeText(): String {
            val raw = phone.trim()
            val digits = if (raw.contains("wa.me/", ignoreCase = true)) {
                raw.substringAfter("wa.me/", "").substringBefore('?').filter { it.isDigit() }
            } else {
                raw.filter { it.isDigit() }
            }
            return if (text.isBlank()) "https://wa.me/$digits"
            else "https://wa.me/$digits?text=${encodeQuery(text)}"
        }
    }

    data class AppStore(val url: String) : QrPayload() {
        override fun encodeText(): String = url.trim()
    }

    data class SocialUrl(val url: String) : QrPayload() {
        override fun encodeText(): String = url.trim()
    }

    data class MeCard(val name: String, val phone: String = "", val email: String = "") : QrPayload() {
        override fun encodeText(): String = buildString {
            append("MECARD:N:").append(name.trim()).append(';')
            if (phone.isNotBlank()) append("TEL:").append(phone.trim()).append(';')
            if (email.isNotBlank()) append("EMAIL:").append(email.trim()).append(';')
            append(';')
        }
    }

    data class FaceTime(val target: String, val audio: Boolean = false) : QrPayload() {
        override fun encodeText(): String {
            val scheme = if (audio) "facetime-audio" else "facetime"
            return "$scheme:${target.trim()}"
        }
    }
}

enum class WifiSecurity { NOPASS, WEP, WPA }

private fun escapeWifi(value: String): String =
    value.replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace(":", "\\:")
        .replace("\"", "\\\"")

private fun encodeQuery(value: String): String =
    java.net.URLEncoder.encode(value, Charsets.UTF_8.name())
