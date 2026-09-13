package org.qraft.app.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import kotlin.math.min

/** Place a styled QR onto a user photo (poster decorate). */
object QrPoster {
    fun compose(
        photoPath: String,
        qr: Bitmap,
        marginFraction: Float = 0.06f,
        corner: Corner = Corner.BottomRight,
    ): Bitmap? {
        val photo = BitmapFactory.decodeFile(photoPath) ?: return null
        val out = photo.copy(Bitmap.Config.ARGB_8888, true) ?: run {
            photo.recycle()
            return null
        }
        if (out !== photo) photo.recycle()
        val canvas = Canvas(out)
        val side = (min(out.width, out.height) * 0.28f).toInt().coerceAtLeast(64)
        val scaled = Bitmap.createScaledBitmap(qr, side, side, true)
        val margin = (min(out.width, out.height) * marginFraction).toInt()
        val left = when (corner) {
            Corner.BottomRight, Corner.TopRight -> out.width - side - margin
            Corner.BottomLeft, Corner.TopLeft -> margin
        }
        val top = when (corner) {
            Corner.BottomRight, Corner.BottomLeft -> out.height - side - margin
            Corner.TopRight, Corner.TopLeft -> margin
        }
        canvas.drawBitmap(scaled, left.toFloat(), top.toFloat(), null)
        if (scaled !== qr) scaled.recycle()
        return out
    }

    enum class Corner { BottomRight, BottomLeft, TopRight, TopLeft }
}
