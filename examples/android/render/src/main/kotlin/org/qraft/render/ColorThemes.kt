package org.qraft.render

enum class ColorTheme { CLASSIC, OLED, INVERT, BLUE, FOREST, SUNSET }

enum class CenterMark {
    NONE, CUTOUT, LINK, WIFI, PERSON, EMAIL, PHONE, SMS, CRYPTO, GITHUB, MASTODON, MATRIX, GLOBE, AUTO,
}

data class GradientSpec(
    val enabled: Boolean = false,
    val startArgb: Int = 0xFF000000.toInt(),
    val endArgb: Int = 0xFF1565C0.toInt(),
)

data class CaptionSpec(val text: String = "")

object ColorThemes {
    fun apply(theme: ColorTheme, style: QrStyle): QrStyle {
        val (fg, bg, eye) = colors(theme)
        return style.copy(
            colorTheme = theme,
            foregroundArgb = fg,
            backgroundArgb = bg,
            eyeColorArgb = eye,
        )
    }

    fun colors(theme: ColorTheme): Triple<Int, Int, Int> = when (theme) {
        ColorTheme.CLASSIC -> Triple(BLACK, WHITE, BLACK)
        ColorTheme.OLED -> Triple(WHITE, OLED, WHITE)
        ColorTheme.INVERT -> Triple(WHITE, BLACK, WHITE)
        ColorTheme.BLUE -> Triple(0xFF0D47A1.toInt(), WHITE, 0xFF0D47A1.toInt())
        ColorTheme.FOREST -> Triple(0xFF1B5E20.toInt(), 0xFFF1F8E9.toInt(), 0xFF1B5E20.toInt())
        ColorTheme.SUNSET -> Triple(0xFFBF360C.toInt(), 0xFFFFF3E0.toInt(), 0xFFE65100.toInt())
    }

    private val BLACK = 0xFF000000.toInt()
    private val WHITE = 0xFFFFFFFF.toInt()
    private val OLED = 0xFF000000.toInt()
}
