package org.qraft.scan

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

enum class Barcode1dKind {
    Code128,
    Ean13,
    Code39,
}

data class BarcodeMatrix(
    val width: Int,
    val height: Int,
    val black: BooleanArray,
) {
    fun isBlack(x: Int, y: Int): Boolean = black[y * width + x]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BarcodeMatrix) return false
        return width == other.width && height == other.height && black.contentEquals(other.black)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + black.contentHashCode()
        return result
    }
}

/** FOSS 1D barcode encode via ZXing (separate from Nayuki QR path). */
object BarcodeEncoder {
    fun encode(kind: Barcode1dKind, raw: String, width: Int = 512, height: Int = 160): BarcodeMatrix? {
        val data = raw.trim()
        if (data.isEmpty() || width <= 0 || height <= 0) return null
        val format = when (kind) {
            Barcode1dKind.Code128 -> BarcodeFormat.CODE_128
            Barcode1dKind.Ean13 -> BarcodeFormat.EAN_13
            Barcode1dKind.Code39 -> BarcodeFormat.CODE_39
        }
        if (kind == Barcode1dKind.Ean13 && !data.all { it.isDigit() }) return null
        if (kind == Barcode1dKind.Ean13 && data.length !in 12..13) return null
        return runCatching {
            val matrix: BitMatrix = MultiFormatWriter().encode(
                data,
                format,
                width,
                height,
                mapOf(EncodeHintType.MARGIN to 2),
            )
            val black = BooleanArray(matrix.width * matrix.height)
            for (y in 0 until matrix.height) {
                for (x in 0 until matrix.width) {
                    black[y * matrix.width + x] = matrix[x, y]
                }
            }
            BarcodeMatrix(matrix.width, matrix.height, black)
        }.getOrNull()
    }

    fun toArgb(matrix: BarcodeMatrix, fg: Int = 0xFF000000.toInt(), bg: Int = 0xFFFFFFFF.toInt()): IntArray {
        val out = IntArray(matrix.width * matrix.height)
        for (i in out.indices) {
            out[i] = if (matrix.black[i]) fg else bg
        }
        return out
    }
}
