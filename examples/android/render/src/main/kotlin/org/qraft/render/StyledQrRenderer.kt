package org.qraft.render

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import org.qraft.coreqr.QrMatrix

object StyledQrRenderer {
    fun render(
        matrix: QrMatrix,
        sizePx: Int,
        style: QrStyle = QrStyle.DEFAULT,
        extras: RasterExtras = RasterExtras(),
        applyCaption: Boolean = true,
    ): Bitmap {
        val square = StyledQrRasterizer.rasterize(matrix, sizePx, style, extras)
        val raster = if (applyCaption) QrCaption.compose(square, style) else square
        val bmp = Bitmap.createBitmap(raster.pixels, raster.width, raster.height, Bitmap.Config.ARGB_8888)
        val text = style.caption.text.trim()
        if (applyCaption && text.isNotEmpty()) {
            val canvas = Canvas(bmp)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.foregroundArgb
                textAlign = Paint.Align.CENTER
                typeface = Typeface.SANS_SERIF
                textSize = (raster.width / 14f).coerceAtLeast(12f)
            }
            val baseline = raster.height - (QrCaption.extraHeight(square, style) / 3f)
            canvas.drawText(text, raster.width / 2f, baseline, paint)
        }
        return bmp
    }
}
