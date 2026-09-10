package org.qraft.render

import org.json.JSONObject

object QrStyleJson {
    fun encode(style: QrStyle): String = JSONObject().apply {
        put("moduleShape", style.moduleShape.name)
        put("finderShape", style.finderShape.name)
        put("finderPupil", style.finderPupil.name)
        put("foregroundArgb", style.foregroundArgb)
        put("eyeColorArgb", style.eyeColorArgb)
        put("backgroundArgb", style.backgroundArgb)
        put("quietZoneModules", style.quietZoneModules)
        put("logoEnabled", style.logoCutout.enabled)
        put("logoDiameterFraction", style.logoCutout.diameterFraction)
        put("colorTheme", style.colorTheme.name)
        put("gradientEnabled", style.gradient.enabled)
        put("gradientStartArgb", style.gradient.startArgb)
        put("gradientEndArgb", style.gradient.endArgb)
        put("centerMark", style.centerMark.name)
        put("caption", style.caption.text)
        put("imageBackgroundPath", style.imageBackgroundPath)
        put("frame", style.frame.name)
        put("cornerBadge", style.cornerBadge)
    }.toString()

    fun decode(json: String): QrStyle {
        if (json.isBlank() || json == "{}") return QrStyle.DEFAULT
        return try {
            val o = JSONObject(json)
            QrStyle(
                moduleShape = enumValueOr(o.optString("moduleShape"), ModuleShape.SQUARE),
                finderShape = enumValueOr(o.optString("finderShape"), FinderShape.SQUARE),
                finderPupil = enumValueOr(o.optString("finderPupil"), FinderShape.SQUARE),
                foregroundArgb = o.optInt("foregroundArgb", QrStyle.DEFAULT.foregroundArgb),
                eyeColorArgb = o.optInt("eyeColorArgb", QrStyle.DEFAULT.eyeColorArgb),
                backgroundArgb = o.optInt("backgroundArgb", QrStyle.DEFAULT.backgroundArgb),
                quietZoneModules = o.optInt("quietZoneModules", QrStyle.DEFAULT.quietZoneModules),
                logoCutout = LogoCutout(
                    enabled = o.optBoolean("logoEnabled", false),
                    diameterFraction = o.optDouble("logoDiameterFraction", LogoCutout().diameterFraction),
                ),
                colorTheme = enumValueOr(o.optString("colorTheme"), ColorTheme.CLASSIC),
                gradient = GradientSpec(
                    enabled = o.optBoolean("gradientEnabled", false),
                    startArgb = o.optInt("gradientStartArgb", GradientSpec().startArgb),
                    endArgb = o.optInt("gradientEndArgb", GradientSpec().endArgb),
                ),
                centerMark = enumValueOr(o.optString("centerMark"), CenterMark.NONE),
                caption = CaptionSpec(o.optString("caption", "")),
                imageBackgroundPath = o.optString("imageBackgroundPath", ""),
                frame = enumValueOr(o.optString("frame"), QrFrame.NONE),
                cornerBadge = o.optBoolean("cornerBadge", false),
            )
        } catch (_: Exception) {
            QrStyle.DEFAULT
        }
    }

    private inline fun <reified T : Enum<T>> enumValueOr(raw: String, fallback: T): T =
        runCatching { java.lang.Enum.valueOf(T::class.java, raw) }.getOrDefault(fallback)
}
