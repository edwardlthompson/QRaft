package org.qraft.scan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import java.io.InputStream
import java.nio.ByteBuffer

data class DecodeHit(
    val text: String,
    val format: String = BarcodeFormat.QR_CODE.name,
)

/** Offline ZXing decode from bitmaps / camera Y planes / gallery photos. */
object QrDecoder {
    private const val MAX_PHOTO_EDGE = 1600

    private val qrFormats = listOf(BarcodeFormat.QR_CODE)
    private val allFormats = listOf(
        BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.PDF_417, BarcodeFormat.CODE_128, BarcodeFormat.CODE_39,
        BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E, BarcodeFormat.ITF, BarcodeFormat.CODABAR,
    )

    fun decodeBitmap(bitmap: Bitmap): String? = decodeBitmapHit(bitmap)?.text

    fun decodeBitmapHit(bitmap: Bitmap): DecodeHit? {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= 0 || h <= 0) return null
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        return decodeArgbHit(pixels, w, h)
            ?: YPlaneOps.halfResArgb(pixels, w, h)?.let { decodeArgbHit(it.pixels, it.width, it.height) }
    }

    fun decodeArgb(pixels: IntArray, width: Int, height: Int): String? =
        decodeArgbHit(pixels, width, height)?.text

    fun decodeArgbHit(pixels: IntArray, width: Int, height: Int): DecodeHit? {
        if (width <= 0 || height <= 0 || pixels.size < width * height) return null
        return decodeSourceHit(RGBLuminanceSource(width, height, pixels))
    }

    fun decodePhoto(stream: InputStream): String? = decodePhotoHit(stream)?.text

    fun decodePhotoHit(stream: InputStream): DecodeHit? {
        val bytes = stream.readBytes()
        if (bytes.isEmpty()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val sample = YPlaneOps.sampleSize(bounds.outWidth, bounds.outHeight, MAX_PHOTO_EDGE)
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val raw = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts) ?: return null
        val oriented = YPlaneOps.applyExif(bytes, raw)
        return try {
            decodeBitmapHit(oriented)
        } finally {
            if (oriented !== raw) oriented.recycle()
            raw.recycle()
        }
    }

    fun decodeYPlane(
        yBuffer: ByteBuffer,
        width: Int,
        height: Int,
        rowStride: Int,
        rotationDegrees: Int = 0,
    ): String? = decodeYPlaneHit(yBuffer, width, height, rowStride, rotationDegrees)?.text

    fun decodeYPlaneHit(
        yBuffer: ByteBuffer,
        width: Int,
        height: Int,
        rowStride: Int,
        rotationDegrees: Int = 0,
    ): DecodeHit? {
        if (width <= 0 || height <= 0 || rowStride < width) return null
        val packed = ByteArray(width * height)
        val dup = yBuffer.duplicate()
        var out = 0
        for (row in 0 until height) {
            dup.position(row * rowStride)
            dup.get(packed, out, width)
            out += width
        }
        val rotated = YPlaneOps.rotateY(packed, width, height, rotationDegrees)
        val full = PlanarYUVLuminanceSource(
            rotated.bytes, rotated.width, rotated.height, 0, 0, rotated.width, rotated.height, false,
        )
        decodeSourceHit(full)?.let { return it }
        val half = YPlaneOps.downsampleY(rotated.bytes, rotated.width, rotated.height)
        return decodeSourceHit(
            PlanarYUVLuminanceSource(
                half.bytes, half.width, half.height, 0, 0, half.width, half.height, false,
            ),
        )
    }

    internal fun sampleSize(width: Int, height: Int, maxEdge: Int): Int =
        YPlaneOps.sampleSize(width, height, maxEdge)

    private fun decodeSourceHit(source: LuminanceSource): DecodeHit? {
        tryFormats(source, qrFormats)?.let { return it }
        return tryFormats(source, allFormats)
    }

    private fun tryFormats(source: LuminanceSource, formats: List<BarcodeFormat>): DecodeHit? {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to formats,
            DecodeHintType.TRY_HARDER to true,
            DecodeHintType.CHARACTER_SET to "UTF-8",
        )
        val reader = MultiFormatReader().also { it.setHints(hints) }
        return try {
            toHit(reader.decodeWithState(BinaryBitmap(HybridBinarizer(source))))
        } catch (_: NotFoundException) {
            try {
                reader.reset()
                toHit(reader.decodeWithState(BinaryBitmap(HybridBinarizer(source.invert()))))
            } catch (_: NotFoundException) {
                null
            }
        } finally {
            reader.reset()
        }
    }

    private fun toHit(result: Result): DecodeHit =
        DecodeHit(text = result.text, format = result.barcodeFormat.name)
}
