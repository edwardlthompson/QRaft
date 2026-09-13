package org.qraft.scan

/** Structured WIFI: payload fields for join / copy. */
data class ParsedWifi(
    val ssid: String,
    val password: String,
    val security: String,
    val hidden: Boolean,
)

object WifiScanParse {
    fun parse(raw: String): ParsedWifi? {
        val text = raw.trim()
        if (!text.startsWith("WIFI:", ignoreCase = true)) return null
        val body = text.removePrefix("WIFI:").removePrefix("wifi:")
        val fields = linkedMapOf<String, String>()
        var i = 0
        while (i < body.length) {
            val colon = body.indexOf(':', i)
            if (colon < 0) break
            val key = body.substring(i, colon)
            i = colon + 1
            val value = StringBuilder()
            while (i < body.length) {
                val c = body[i]
                if (c == '\\' && i + 1 < body.length) {
                    value.append(body[i + 1])
                    i += 2
                    continue
                }
                if (c == ';') {
                    i++
                    break
                }
                value.append(c)
                i++
            }
            if (key.isNotEmpty()) fields[key.uppercase()] = value.toString()
        }
        val ssid = fields["S"] ?: return null
        return ParsedWifi(
            ssid = ssid,
            password = fields["P"].orEmpty(),
            security = fields["T"] ?: "nopass",
            hidden = fields["H"].equals("true", ignoreCase = true),
        )
    }
}
