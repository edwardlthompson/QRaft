package org.qraft.app.ui.stylepanel

import org.qraft.render.FinderShape
import org.qraft.render.QrStyle

object StyleLookFold {
    fun withFinder(style: QrStyle, shape: FinderShape): QrStyle =
        style.copy(finderShape = shape, finderPupil = shape)
}
