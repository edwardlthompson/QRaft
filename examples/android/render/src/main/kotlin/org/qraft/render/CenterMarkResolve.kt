package org.qraft.render

/** Maps AUTO (and payload prefixes) to a drawable center mark. */
object CenterMarkResolve {
    fun of(mark: CenterMark, payload: String = ""): CenterMark {
        if (mark != CenterMark.AUTO) return mark
        val t = payload.trim()
        return when {
            t.startsWith("WIFI:", ignoreCase = true) -> CenterMark.WIFI
            t.startsWith("BEGIN:VCARD", ignoreCase = true) -> CenterMark.PERSON
            t.startsWith("MECARD:", ignoreCase = true) -> CenterMark.PERSON
            t.startsWith("mailto:", ignoreCase = true) -> CenterMark.EMAIL
            t.startsWith("SMSTO:", ignoreCase = true) || t.startsWith("sms:", ignoreCase = true) -> CenterMark.SMS
            t.startsWith("tel:", ignoreCase = true) || t.startsWith("facetime", ignoreCase = true) -> CenterMark.PHONE
            t.startsWith("geo:", ignoreCase = true) -> CenterMark.GLOBE
            t.startsWith("bitcoin:", ignoreCase = true) || t.startsWith("ethereum:", ignoreCase = true) ->
                CenterMark.CRYPTO
            t.contains("github.com", ignoreCase = true) -> CenterMark.GITHUB
            t.contains("mastodon", ignoreCase = true) -> CenterMark.MASTODON
            t.startsWith("http://", ignoreCase = true) || t.startsWith("https://", ignoreCase = true) ->
                CenterMark.LINK
            t.isBlank() -> CenterMark.NONE
            else -> CenterMark.GLOBE
        }
    }
}

internal fun RasterExtras.withGeneratedInlay(style: QrStyle, payloadHint: String = ""): RasterExtras {
    if (inlayPixels != null) return this
    val mark = CenterMarkResolve.of(style.centerMark, payloadHint)
    if (mark == CenterMark.NONE || mark == CenterMark.CUTOUT) return this
    val (pixels, w, h) = CenterMarkIcons.bitmap(mark)
    return copy(inlayPixels = pixels, inlayWidth = w, inlayHeight = h)
}
