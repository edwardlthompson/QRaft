package org.qraft.render

object GradientPaint {
    fun moduleColor(style: QrStyle, x: Int, size: Int): Int {
        if (!style.gradient.enabled) return style.foregroundArgb
        val t = if (size <= 1) 0.0 else x / (size - 1.0)
        return lerp(style.gradient.startArgb, style.gradient.endArgb, t)
    }

    fun lerp(start: Int, end: Int, t: Double): Int {
        val u = t.coerceIn(0.0, 1.0)
        fun ch(value: Int, shift: Int): Int {
            val a = (start shr shift) and 0xFF
            val b = (end shr shift) and 0xFF
            return (a + (b - a) * u).toInt()
        }
        return (ch(start, 24) shl 24) or (ch(start, 16) shl 16) or (ch(start, 8) shl 8) or ch(start, 0)
    }
}
