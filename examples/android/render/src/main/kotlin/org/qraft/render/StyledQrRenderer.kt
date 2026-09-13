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
        val base = Bitmap.createBitmap(square.pixels, square.width, square.height, Bitmap.Config.ARGB_8888)
        return if (applyCaption) withCaption(base, style, recycleSource = true) else base
    }

    /**
     * Adds / refreshes the caption band under an existing square QR bitmap.
     * When [recycleSource] is true and a taller bitmap is allocated, [source] is recycled.
     */
    fun withCaption(source: Bitmap, style: QrStyle, recycleSource: Boolean = false): Bitmap {
        val text = style.caption.text.trim()
        if (text.isEmpty()) return source
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.foregroundArgb
            textAlign = Paint.Align.CENTER
            typeface = Typeface.SANS_SERIF
            textSize = (source.width / 14f).coerceAtLeast(12f)
        }
        val fm = paint.fontMetrics
        val textHeight = fm.descent - fm.ascent
        val padTop = (textHeight * 0.12f).toInt().coerceAtLeast(2)
        val padBottom = (textHeight * 0.18f).toInt().coerceAtLeast(2)
        val extra = (padTop + textHeight + padBottom).toInt().coerceAtLeast(QrCaption.bandHeightPx(source.width))
        val out = Bitmap.createBitmap(source.width, source.height + extra, Bitmap.Config.ARGB_8888)
        out.eraseColor(style.backgroundArgb)
        val canvas = Canvas(out)
        canvas.drawBitmap(source, 0f, 0f, null)
        // Baseline sits just below the QR with a thin pad — no second quiet-zone gap.
        val baseline = source.height + padTop - fm.ascent
        canvas.drawText(text, source.width / 2f, baseline, paint)
        if (recycleSource && source !== out && !source.isRecycled) source.recycle()
        return out
    }
}
