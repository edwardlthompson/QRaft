package org.qraft.wallpaper

import org.qraft.coreqr.QrMatrix
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRasterizer

data class WallpaperImage(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WallpaperImage) return false
        return width == other.width && height == other.height && pixels.contentEquals(other.pixels)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + pixels.contentHashCode()
        return result
    }
}

fun interface WallpaperSetter {
    fun setPixels(image: WallpaperImage)
}

class WallpaperApplier(private val setter: WallpaperSetter) {
    fun apply(image: WallpaperImage) {
        require(image.width > 0 && image.height > 0)
        require(image.pixels.size == image.width * image.height)
        setter.setPixels(image)
    }
}

object WallpaperComposer {
    fun compose(
        matrix: QrMatrix,
        totalWidthPx: Int,
        totalHeightPx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        marginFraction: Double = WallpaperSafeZone.DEFAULT_MARGIN_FRACTION,
    ): WallpaperImage {
        require(totalWidthPx > 0 && totalHeightPx > 0)
        val qrRect = WallpaperSafeZone.qrContentRect(
            totalWidthPx,
            totalHeightPx,
            WallpaperSafeZone.clampUserMargin(marginFraction),
        )
        val raster = StyledQrRasterizer.rasterize(matrix, qrRect.width, style)
        val pixels = IntArray(totalWidthPx * totalHeightPx) { style.backgroundArgb }
        blit(raster.pixels, raster.width, raster.height, pixels, totalWidthPx, qrRect.left, qrRect.top)
        return WallpaperImage(totalWidthPx, totalHeightPx, pixels)
    }

    private fun blit(
        src: IntArray,
        srcW: Int,
        srcH: Int,
        dest: IntArray,
        destW: Int,
        left: Int,
        top: Int,
    ) {
        for (y in 0 until srcH) {
            val destY = top + y
            if (destY < 0) continue
            val destRow = destY * destW
            val srcRow = y * srcW
            for (x in 0 until srcW) {
                val destX = left + x
                if (destX in 0 until destW) dest[destRow + destX] = src[srcRow + x]
            }
        }
    }
}
