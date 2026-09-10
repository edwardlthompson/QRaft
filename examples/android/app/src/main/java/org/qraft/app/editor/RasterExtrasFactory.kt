package org.qraft.app.editor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import org.qraft.app.gallery.BgImage
import org.qraft.render.CenterMark
import org.qraft.render.QrStyle
import org.qraft.render.RasterExtras

object RasterExtrasFactory {
    fun of(style: QrStyle, kind: PayloadKind, sizePx: Int = 512): RasterExtras {
        val mark = resolve(style.centerMark, kind)
        val inlay = if (mark == CenterMark.NONE || mark == CenterMark.CUTOUT) {
            Triple(null as IntArray?, 0, 0)
        } else {
            markBitmap(mark)
        }
        return RasterExtras(
            bgPixels = BgImage.pixels(style.imageBackgroundPath, sizePx),
            inlayPixels = inlay.first,
            inlayWidth = inlay.second,
            inlayHeight = inlay.third,
        )
    }

    fun resolve(mark: CenterMark, kind: PayloadKind): CenterMark {
        if (mark != CenterMark.AUTO) return mark
        return when (kind) {
            PayloadKind.Url -> CenterMark.LINK
            PayloadKind.Wifi -> CenterMark.WIFI
            PayloadKind.VCard -> CenterMark.PERSON
            PayloadKind.Email -> CenterMark.EMAIL
            PayloadKind.Phone -> CenterMark.PHONE
            PayloadKind.Sms -> CenterMark.SMS
            PayloadKind.Crypto -> CenterMark.CRYPTO
            PayloadKind.Text -> CenterMark.GLOBE
        }
    }

    private fun markBitmap(mark: CenterMark): Triple<IntArray, Int, Int> {
        val size = 32
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val fill = Paint().apply { color = 0xFF000000.toInt(); isAntiAlias = true }
        canvas.drawCircle(16f, 16f, 15f, fill)
        val text = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            textAlign = Paint.Align.CENTER
            textSize = 14f
            isAntiAlias = true
        }
        val glyph = when (mark) {
            CenterMark.WIFI -> "W"
            CenterMark.PERSON -> "P"
            CenterMark.EMAIL -> "@"
            CenterMark.PHONE -> "T"
            CenterMark.SMS -> "S"
            CenterMark.CRYPTO -> "C"
            CenterMark.GITHUB -> "G"
            CenterMark.MASTODON -> "M"
            CenterMark.MATRIX -> "X"
            else -> "i"
        }
        canvas.drawText(glyph, 16f, 21f, text)
        val pixels = IntArray(size * size)
        bmp.getPixels(pixels, 0, size, 0, 0, size, size)
        bmp.recycle()
        return Triple(pixels, size, size)
    }
}
