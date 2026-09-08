package org.qraft.coreqr

/**
 * Pure square-module rasterizer. Never stretches modules — leftover pixels become
 * a centered quiet/padding border using [backgroundArgb].
 */
object SquareRasterizer {
    data class Options(
        val foregroundArgb: Int = 0xFF000000.toInt(),
        val backgroundArgb: Int = 0xFFFFFFFF.toInt(),
        val quietZoneModules: Int = Scannability.MIN_QUIET_ZONE_MODULES,
    )

    data class Result(
        val width: Int,
        val height: Int,
        val pixels: IntArray,
        val modulePx: Int,
        val contentOriginX: Int,
        val contentOriginY: Int,
        val contentSizePx: Int,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Result) return false
            return width == other.width &&
                height == other.height &&
                modulePx == other.modulePx &&
                contentOriginX == other.contentOriginX &&
                contentOriginY == other.contentOriginY &&
                contentSizePx == other.contentSizePx &&
                pixels.contentEquals(other.pixels)
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + pixels.contentHashCode()
            result = 31 * result + modulePx
            result = 31 * result + contentOriginX
            result = 31 * result + contentOriginY
            result = 31 * result + contentSizePx
            return result
        }
    }

    fun rasterize(matrix: QrMatrix, sizePx: Int, options: Options = Options()): Result {
        require(sizePx > 0) { "sizePx must be > 0" }
        require(options.quietZoneModules >= 0)
        val modulesWithQuiet = matrix.size + 2 * options.quietZoneModules
        val modulePx = maxOf(1, sizePx / modulesWithQuiet)
        val contentSizePx = modulePx * modulesWithQuiet
        val originX = (sizePx - contentSizePx) / 2
        val originY = (sizePx - contentSizePx) / 2
        val pixels = IntArray(sizePx * sizePx) { options.backgroundArgb }

        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (!matrix.isDark(x, y)) continue
                val left = originX + (x + options.quietZoneModules) * modulePx
                val top = originY + (y + options.quietZoneModules) * modulePx
                fillRect(pixels, sizePx, left, top, modulePx, modulePx, options.foregroundArgb)
            }
        }
        return Result(
            width = sizePx,
            height = sizePx,
            pixels = pixels,
            modulePx = modulePx,
            contentOriginX = originX,
            contentOriginY = originY,
            contentSizePx = contentSizePx,
        )
    }

    private fun fillRect(
        pixels: IntArray,
        stride: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
        color: Int,
    ) {
        val right = minOf(stride, left + width)
        val bottom = minOf(stride, top + height)
        val x0 = maxOf(0, left)
        val y0 = maxOf(0, top)
        for (y in y0 until bottom) {
            val row = y * stride
            for (x in x0 until right) {
                pixels[row + x] = color
            }
        }
    }
}
