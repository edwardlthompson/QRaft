package org.qraft.widget

import org.qraft.render.QrStyle

object WidgetPaint {
    private val WHITE = 0xFFFFFFFF.toInt()

    /** Transparent clears the plate; opaque always keeps a light scannable background. */
    fun style(base: QrStyle, transparent: Boolean): QrStyle =
        if (transparent) {
            base.copy(backgroundArgb = 0)
        } else {
            val alpha = (base.backgroundArgb ushr 24) and 0xFF
            if (alpha < 16) base.copy(backgroundArgb = WHITE) else base
        }
}
