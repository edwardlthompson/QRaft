package org.qraft.widget

import org.qraft.render.QrStyle

object WidgetPaint {
    fun style(base: QrStyle, transparent: Boolean): QrStyle =
        if (transparent) base.copy(backgroundArgb = 0) else base
}
