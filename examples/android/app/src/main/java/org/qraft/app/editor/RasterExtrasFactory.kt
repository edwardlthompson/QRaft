package org.qraft.app.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.qraft.app.gallery.BgImage
import org.qraft.render.CenterMark
import org.qraft.render.QrStyle
import org.qraft.render.RasterExtras

object RasterExtrasFactory {
    fun of(style: QrStyle, kind: PayloadKind, sizePx: Int = 512): RasterExtras {
        val logo = logoInlay(style.logoImagePath)
        val mark = resolve(style.centerMark, kind)
        val inlay = when {
            logo != null -> logo
            mark == CenterMark.NONE || mark == CenterMark.CUTOUT -> Triple(null as IntArray?, 0, 0)
            else -> CenterMarkIcons.bitmap(mark)
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
            PayloadKind.Url, PayloadKind.AppStore -> CenterMark.LINK
            PayloadKind.Wifi -> CenterMark.WIFI
            PayloadKind.VCard -> CenterMark.PERSON
            PayloadKind.Email -> CenterMark.EMAIL
            PayloadKind.Phone, PayloadKind.WhatsApp -> CenterMark.PHONE
            PayloadKind.Sms -> CenterMark.SMS
            PayloadKind.Crypto -> CenterMark.CRYPTO
            PayloadKind.Social -> CenterMark.MASTODON
            PayloadKind.Calendar, PayloadKind.Geo, PayloadKind.Text, PayloadKind.MeCard -> CenterMark.GLOBE
            PayloadKind.FaceTime -> CenterMark.PHONE
            PayloadKind.Barcode -> CenterMark.NONE
        }
    }

    private fun logoInlay(path: String): Triple<IntArray?, Int, Int>? {
        if (path.isBlank()) return null
        val decoded = BitmapFactory.decodeFile(path) ?: return null
        val size = 64
        val square = Bitmap.createScaledBitmap(decoded, size, size, true)
        val pixels = IntArray(size * size)
        square.getPixels(pixels, 0, size, 0, 0, size, size)
        if (square !== decoded) square.recycle()
        decoded.recycle()
        return Triple(pixels, size, size)
    }
}
