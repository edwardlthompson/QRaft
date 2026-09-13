package org.qraft.widget

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object WidgetSize {
    const val RENDER_PX = 512
    const val GLANCE_PX = WidgetQrCanvas.MAX_BITMAP_PX

    fun cells(widthDp: Int, heightDp: Int): Int {
        val n = minOf(widthDp, heightDp).coerceAtLeast(0)
        return when {
            n < 80 -> 1
            n < 160 -> 2
            else -> 3
        }
    }

    fun cells(size: DpSize): Int =
        cells(size.width.value.toInt(), size.height.value.toInt())

    fun showChrome(size: DpSize): Boolean =
        WidgetQrCanvas.showChrome(size.width.value, size.height.value)

    fun usableSideDp(size: DpSize): Dp {
        val chrome = showChrome(size)
        return WidgetQrCanvas.usableSideDp(size.width.value, size.height.value, chrome).dp
    }

    fun exactPx(density: Float, size: DpSize): Int {
        val side = WidgetQrCanvas.usableSideDp(
            size.width.value,
            size.height.value,
            showChrome(size),
        )
        return WidgetQrCanvas.targetPx(density, side)
    }

    fun minSide(size: DpSize): Dp = minOf(size.width, size.height)
}
