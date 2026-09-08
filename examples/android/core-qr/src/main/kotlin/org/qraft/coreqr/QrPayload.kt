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
        val fullName: String,
        val phone: String = "",
        val email: String = "",
        val org: String = "",
        val url: String = "",
    ) : QrPayload() {
        override fun encodeText(): String = buildString {
            append("BEGIN:VCARD\nVERSION:3.0\n")
            append("FN:").append(fullName).append('\n')
            if (org.isNotBlank()) append("ORG:").append(org).append('\n')
            if (phone.isNotBlank()) append("TEL:").append(phone).append('\n')
            if (email.isNotBlank()) append("EMAIL:").append(email).append('\n')
            if (url.isNotBlank()) append("URL:").append(url).append('\n')
            append("END:VCARD")
        }
    }

    data class Email(val address: String, val subject: String = "", val body: String = "") : QrPayload() {
        override fun encodeText(): String {
            if (subject.isBlank() && body.isBlank()) return "mailto:${address.trim()}"
            val q = buildList {
                if (subject.isNotBlank()) add("subject=${encodeQuery(subject)}")
                if (body.isNotBlank()) add("body=${encodeQuery(body)}")
            }.joinToString("&")
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
