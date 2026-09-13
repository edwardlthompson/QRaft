package org.qraft.wallpaper

import android.graphics.Bitmap
import org.qraft.coreqr.QrMatrix
import org.qraft.render.QrCaption
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRasterizer
import org.qraft.render.StyledQrRenderer
import kotlin.math.min

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
    const val LOCK_LEFTOVER_ARGB: Int = 0xFF000000.toInt()

    fun compose(
        matrix: QrMatrix,
        totalWidthPx: Int,
        totalHeightPx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        marginFraction: Double = WallpaperSafeZone.DEFAULT_MARGIN_FRACTION,
        leftoverArgb: Int? = null,
        applyCaption: Boolean = false,
    ): WallpaperImage {
        require(totalWidthPx > 0 && totalHeightPx > 0)
        val margin = WallpaperSafeZone.clampUserMargin(marginFraction)
        val fill = leftoverArgb ?: style.backgroundArgb
        val withCaption = applyCaption && style.caption.text.isNotBlank()
        val safe = WallpaperSafeZone.safeRectangle(totalWidthPx, totalHeightPx, margin)
        val side = if (withCaption) {
            maxSideForCaption(safe.width, safe.height)
        } else {
            WallpaperSafeZone.qrContentRect(totalWidthPx, totalHeightPx, margin).width
        }
        val (src, srcW, srcH) = rasterBlock(matrix, side, style, withCaption)
        val left = safe.left + (safe.width - srcW) / 2
        val top = safe.top + (safe.height - srcH) / 2
        val pixels = IntArray(totalWidthPx * totalHeightPx) { fill }
        blit(src, srcW, srcH, pixels, totalWidthPx, left, top)
        return WallpaperImage(totalWidthPx, totalHeightPx, pixels)
    }

    /** Lock screen: caption under the QR, unused area solid black. */
    fun composeLock(
        matrix: QrMatrix,
        totalWidthPx: Int,
        totalHeightPx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        marginFraction: Double = WallpaperSafeZone.DEFAULT_MARGIN_FRACTION,
    ): WallpaperImage = compose(
        matrix = matrix,
        totalWidthPx = totalWidthPx,
        totalHeightPx = totalHeightPx,
        style = style,
        marginFraction = marginFraction,
        leftoverArgb = LOCK_LEFTOVER_ARGB,
        applyCaption = true,
    )

    /** Largest QR side that fits with a snug caption band inside [maxW]×[maxH]. */
    fun maxSideForCaption(maxW: Int, maxH: Int): Int {
        var best = 1
        var lo = 1
        var hi = min(maxW, maxH).coerceAtLeast(1)
        while (lo <= hi) {
            val mid = (lo + hi) / 2
            val needH = mid + QrCaption.bandHeightPx(mid)
            if (mid <= maxW && needH <= maxH) {
                best = mid
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
        return best
    }

    private fun rasterBlock(
        matrix: QrMatrix,
        side: Int,
        style: QrStyle,
        withCaption: Boolean,
    ): Triple<IntArray, Int, Int> {
        val raster = StyledQrRasterizer.rasterize(matrix, side, style)
        if (!withCaption) return Triple(raster.pixels, raster.width, raster.height)
        val base = Bitmap.createBitmap(raster.pixels, raster.width, raster.height, Bitmap.Config.ARGB_8888)
        val tall = StyledQrRenderer.withCaption(base, style, recycleSource = true)
        val out = IntArray(tall.width * tall.height)
        tall.getPixels(out, 0, tall.width, 0, 0, tall.width, tall.height)
        val w = tall.width
        val h = tall.height
        if (!tall.isRecycled) tall.recycle()
        return Triple(out, w, h)
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
