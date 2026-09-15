package org.qraft.render

/**
 * Streamline Ultimate Color (CC BY 4.0) on a white rounded plate.
 * Cutout in the QR matches this plate.
 */
object CenterMarkIcons {
    const val WHITE = 0xFFFFFFFF.toInt()
    const val CORNER = 0.14
    const val PAD = 0.12f

    fun bitmap(mark: CenterMark, size: Int = 128): Triple<IntArray, Int, Int> {
        val pixels = IntArray(size * size)
        val corner = (size * CORNER).toInt().coerceAtLeast(1)
        RasterDraw.fillRoundedRect(pixels, size, 0, 0, size, size, corner, WHITE)
        SvgIconRaster.blit(pixels, size, svg(mark), size * PAD)
        return Triple(pixels, size, size)
    }

    fun svg(mark: CenterMark): String {
        val name = fileName(mark)
        val stream = CenterMarkIcons::class.java.getResourceAsStream("icons/$name.svg")
            ?: error("missing center-mark icon $name")
        return stream.bufferedReader().use { it.readText() }
    }

    fun fileName(mark: CenterMark): String = when (mark) {
        CenterMark.WIFI -> "wifi"
        CenterMark.PERSON -> "person"
        CenterMark.EMAIL -> "email"
        CenterMark.PHONE -> "phone"
        CenterMark.SMS -> "sms"
        CenterMark.CRYPTO -> "crypto"
        CenterMark.GITHUB -> "code"
        CenterMark.MASTODON, CenterMark.MATRIX -> "share"
        CenterMark.GLOBE -> "globe"
        else -> "link"
    }
}
