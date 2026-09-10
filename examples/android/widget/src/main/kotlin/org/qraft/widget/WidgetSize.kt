package org.qraft.widget

object WidgetSize {
    const val RENDER_PX = 512

    fun cells(widthDp: Int, heightDp: Int): Int {
        val n = minOf(widthDp, heightDp).coerceAtLeast(0)
        return when {
            n < 80 -> 1
            n < 160 -> 2
            else -> 3
        }
    }
}
