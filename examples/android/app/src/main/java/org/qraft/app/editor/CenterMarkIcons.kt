package org.qraft.app.editor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import org.qraft.render.CenterMark

/** Geometric Material-style center marks (hand-drawn paths, not generated art). */
object CenterMarkIcons {
    fun bitmap(mark: CenterMark, size: Int = 64): Triple<IntArray, Int, Int> {
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF111111.toInt()
            style = Paint.Style.STROKE
            strokeWidth = size * 0.08f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF111111.toInt()
            style = Paint.Style.FILL
        }
        val pad = size * 0.18f
        val box = RectF(pad, pad, size - pad, size - pad)
        when (mark) {
            CenterMark.WIFI -> wifi(canvas, box, stroke)
            CenterMark.PERSON -> person(canvas, box, fill)
            CenterMark.EMAIL -> envelope(canvas, box, stroke)
            CenterMark.PHONE -> phone(canvas, box, stroke)
            CenterMark.SMS -> chat(canvas, box, stroke)
            CenterMark.CRYPTO -> diamond(canvas, box, stroke)
            CenterMark.GITHUB -> chevrons(canvas, box, stroke)
            CenterMark.MASTODON, CenterMark.MATRIX -> share(canvas, box, stroke)
            CenterMark.GLOBE -> globe(canvas, box, stroke)
            else -> link(canvas, box, stroke)
        }
        val pixels = IntArray(size * size)
        bmp.getPixels(pixels, 0, size, 0, 0, size, size)
        bmp.recycle()
        return Triple(pixels, size, size)
    }

    private fun wifi(c: Canvas, b: RectF, p: Paint) {
        val cx = b.centerX()
        val cy = b.bottom - b.height() * 0.12f
        c.drawCircle(cx, cy, p.strokeWidth * 0.4f, Paint(p).apply { style = Paint.Style.FILL })
        c.drawArc(cx - b.width() * 0.28f, cy - b.height() * 0.42f, cx + b.width() * 0.28f, cy + b.height() * 0.18f, 200f, 140f, false, p)
        c.drawArc(cx - b.width() * 0.48f, cy - b.height() * 0.72f, cx + b.width() * 0.48f, cy + b.height() * 0.28f, 200f, 140f, false, p)
    }

    private fun person(c: Canvas, b: RectF, p: Paint) {
        val r = b.width() * 0.16f
        c.drawCircle(b.centerX(), b.top + r * 1.4f, r, p)
        c.drawOval(RectF(b.left + b.width() * 0.12f, b.centerY(), b.right - b.width() * 0.12f, b.bottom), p)
    }

    private fun envelope(c: Canvas, b: RectF, p: Paint) {
        c.drawRect(b, p)
        c.drawLine(b.left, b.top, b.centerX(), b.centerY(), p)
        c.drawLine(b.right, b.top, b.centerX(), b.centerY(), p)
    }

    private fun phone(c: Canvas, b: RectF, p: Paint) {
        c.drawRoundRect(b, b.width() * 0.18f, b.width() * 0.18f, p)
        c.drawLine(b.centerX() - b.width() * 0.12f, b.bottom - b.height() * 0.12f, b.centerX() + b.width() * 0.12f, b.bottom - b.height() * 0.12f, p)
    }

    private fun chat(c: Canvas, b: RectF, p: Paint) {
        val bubble = RectF(b.left, b.top, b.right, b.bottom - b.height() * 0.18f)
        c.drawRoundRect(bubble, b.width() * 0.16f, b.width() * 0.16f, p)
        val tail = Path().apply {
            moveTo(b.left + b.width() * 0.22f, bubble.bottom)
            lineTo(b.left + b.width() * 0.12f, b.bottom)
            lineTo(b.left + b.width() * 0.38f, bubble.bottom)
        }
        c.drawPath(tail, p)
    }

    private fun diamond(c: Canvas, b: RectF, p: Paint) {
        val path = Path().apply {
            moveTo(b.centerX(), b.top)
            lineTo(b.right, b.centerY())
            lineTo(b.centerX(), b.bottom)
            lineTo(b.left, b.centerY())
            close()
        }
        c.drawPath(path, p)
    }

    private fun chevrons(c: Canvas, b: RectF, p: Paint) {
        c.drawLine(b.centerX() - b.width() * 0.12f, b.top + b.height() * 0.22f, b.left + b.width() * 0.12f, b.centerY(), p)
        c.drawLine(b.left + b.width() * 0.12f, b.centerY(), b.centerX() - b.width() * 0.12f, b.bottom - b.height() * 0.22f, p)
        c.drawLine(b.centerX() + b.width() * 0.12f, b.top + b.height() * 0.22f, b.right - b.width() * 0.12f, b.centerY(), p)
        c.drawLine(b.right - b.width() * 0.12f, b.centerY(), b.centerX() + b.width() * 0.12f, b.bottom - b.height() * 0.22f, p)
    }

    private fun share(c: Canvas, b: RectF, p: Paint) {
        val r = p.strokeWidth * 0.7f
        val fill = Paint(p).apply { style = Paint.Style.FILL }
        c.drawCircle(b.right - r * 2, b.top + r * 2, r, fill)
        c.drawCircle(b.left + r * 2, b.centerY(), r, fill)
        c.drawCircle(b.right - r * 2, b.bottom - r * 2, r, fill)
        c.drawLine(b.right - r * 2, b.top + r * 2, b.left + r * 2, b.centerY(), p)
        c.drawLine(b.left + r * 2, b.centerY(), b.right - r * 2, b.bottom - r * 2, p)
    }

    private fun globe(c: Canvas, b: RectF, p: Paint) {
        c.drawOval(b, p)
        c.drawLine(b.centerX(), b.top, b.centerX(), b.bottom, p)
        c.drawLine(b.left, b.centerY(), b.right, b.centerY(), p)
        c.drawArc(b.left + b.width() * 0.22f, b.top, b.right - b.width() * 0.22f, b.bottom, 270f, 180f, false, p)
    }

    private fun link(c: Canvas, b: RectF, p: Paint) {
        val w = b.width() * 0.42f
        val h = b.height() * 0.28f
        val a = RectF(b.left, b.centerY() - h / 2, b.left + w, b.centerY() + h / 2)
        val z = RectF(b.right - w, b.centerY() - h / 2, b.right, b.centerY() + h / 2)
        c.drawRoundRect(a, h / 2, h / 2, p)
        c.drawRoundRect(z, h / 2, h / 2, p)
        c.drawLine(a.right - h * 0.2f, b.centerY(), z.left + h * 0.2f, b.centerY(), p)
    }
}
