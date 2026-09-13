package org.qraft.widget

data class CachedBitmap(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CachedBitmap) return false
        return width == other.width && height == other.height && pixels.contentEquals(other.pixels)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + pixels.contentHashCode()
        return result
    }
}

class QrWidgetCache {
    private val entries = linkedMapOf<String, CachedBitmap>()

    fun key(profileId: String, styleJson: String, sizePx: Int): String =
        "$profileId|$sizePx|$styleJson"

    fun put(key: String, width: Int, height: Int, pixels: IntArray) {
        if (key.isBlank() || width <= 0 || height <= 0 || pixels.size != width * height) return
        entries[key] = CachedBitmap(width, height, pixels.copyOf())
    }

    fun get(key: String): CachedBitmap? = entries[key]?.let {
        it.copy(pixels = it.pixels.copyOf())
    }

    fun clear() {
        entries.clear()
    }
}

object BrightenAction {
    const val ACTION = "org.qraft.widget.action.BRIGHTEN"
    const val WINDOW_BRIGHTNESS = 1.0f
}
