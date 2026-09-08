package org.qraft.render

import android.graphics.Bitmap
import org.qraft.coreqr.QrMatrix
import org.qraft.coreqr.Scannability
import org.qraft.coreqr.SquareRasterizer

/**
 * Android [Bitmap] wrapper around [SquareRasterizer]. Module shapes beyond square
 * land in later style renderers; this path is the scannable baseline.
 */
object SquareQrRenderer {
    data class Options(
        val foregroundArgb: Int = 0xFF000000.toInt(),
        val backgroundArgb: Int = 0xFFFFFFFF.toInt(),
        val quietZoneModules: Int = Scannability.MIN_QUIET_ZONE_MODULES,
        val config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    )

    fun render(matrix: QrMatrix, sizePx: Int, options: Options = Options()): Bitmap {
        val raster = SquareRasterizer.rasterize(
            matrix,
            sizePx,
            SquareRasterizer.Options(
                foregroundArgb = options.foregroundArgb,
                backgroundArgb = options.backgroundArgb,
                quietZoneModules = options.quietZoneModules,
            ),
        )
        return Bitmap.createBitmap(raster.pixels, raster.width, raster.height, options.config)
    }
}
