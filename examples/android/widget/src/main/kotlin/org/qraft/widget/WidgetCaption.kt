package org.qraft.widget

import org.qraft.data.QrProfile
import org.qraft.render.CaptionSpec
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

/** Human-readable label under the widget QR (style caption, else card name). */
object WidgetCaption {
    fun text(profile: QrProfile): String {
        val fromStyle = captionFromStyle(profile.styleJson)
        if (fromStyle.isNotEmpty()) return fromStyle
        return profile.name.trim().ifBlank { "QR" }
    }

    /**
     * Caption burned into the widget bitmap.
     * Style caption always wins; when [nameFallback] is true, card name is used if style caption is blank.
     */
    fun forBitmap(profile: QrProfile, nameFallback: Boolean): String {
        val fromStyle = captionFromStyle(profile.styleJson)
        if (fromStyle.isNotEmpty()) return fromStyle
        if (!nameFallback) return ""
        return profile.name.trim()
    }

    fun styleWithCaption(base: QrStyle, caption: String): QrStyle =
        if (caption.isBlank()) base.copy(caption = CaptionSpec())
        else base.copy(caption = CaptionSpec(caption))

    fun captionFromStyle(styleJson: String): String {
        if (styleJson.isBlank() || styleJson == "{}") return ""
        val decoded = runCatching {
            QrStyleJson.decode(styleJson).caption.text.trim()
        }.getOrDefault("")
        if (decoded.isNotEmpty()) return decoded
        val key = "\"caption\":\""
        val start = styleJson.indexOf(key)
        if (start < 0) return ""
        val from = start + key.length
        val end = styleJson.indexOf('"', from)
        if (end < 0) return ""
        return styleJson.substring(from, end).trim()
    }
}
