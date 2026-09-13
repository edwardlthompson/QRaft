package org.qraft.scan

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

internal data class YFrame(val bytes: ByteArray, val width: Int, val height: Int)
internal data class ArgbFrame(val pixels: IntArray, val width: Int, val height: Int)

internal object YPlaneOps {
    fun rotateY(src: ByteArray, width: Int, height: Int, degrees: Int): YFrame {
        val d = ((degrees % 360) + 360) % 360
        if (d == 0 || src.size < width * height) return YFrame(src, width, height)
        return when (d) {
            90 -> {
                val out = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        out[x * height + (height - 1 - y)] = src[y * width + x]
                    }
                }
                YFrame(out, height, width)
            }
            180 -> {
                val out = ByteArray(width * height)
                val last = width * height - 1
                for (i in 0 until width * height) out[last - i] = src[i]
                YFrame(out, width, height)
            }
            270 -> {
                val out = ByteArray(width * height)
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        out[(width - 1 - x) * height + y] = src[y * width + x]
                    }
                }
                YFrame(out, height, width)
            }
            else -> YFrame(src, width, height)
        }
    }

    fun downsampleY(src: ByteArray, width: Int, height: Int): YFrame {
        val w = (width / 2).coerceAtLeast(1)
        val h = (height / 2).coerceAtLeast(1)
        val out = ByteArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                out[y * w + x] = src[(y * 2) * width + (x * 2)]
            }
        }
        return YFrame(out, w, h)
    }

    fun halfResArgb(pixels: IntArray, width: Int, height: Int): ArgbFrame? {
        if (width < 64 || height < 64) return null
        val w = width / 2
        val h = height / 2
        val out = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                out[y * w + x] = pixels[(y * 2) * width + (x * 2)]
            }
        }
        return ArgbFrame(out, w, h)
    }

    fun sampleSize(width: Int, height: Int, maxEdge: Int): Int {
        if (width <= 0 || height <= 0) return 1
        var sample = 1
        var w = width
        var h = height
        while (w > maxEdge || h > maxEdge) {
            sample *= 2
            w /= 2
            h /= 2
        }
        return sample.coerceAtLeast(1)
    }

    fun applyExif(bytes: ByteArray, bitmap: Bitmap): Bitmap {
        val orient = runCatching {
            ExifInterface(bytes.inputStream()).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        val degrees = when (orient) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
