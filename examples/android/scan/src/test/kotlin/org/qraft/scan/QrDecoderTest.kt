package org.qraft.scan

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class QrDecoderTest {
    @Test
    fun decodesZxingWrittenUrl() {
        val payload = "https://f-droid.org"
        val decoded = QrDecoder.decodeBitmap(qrBitmap(payload))
        assertEquals(payload, decoded)
    }

    @Test
    fun blankBitmapReturnsNull() {
        val blank = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        blank.eraseColor(Color.WHITE)
        assertNull(QrDecoder.decodeBitmap(blank))
    }

    @Test
    fun decodesYPlaneLikeCamera() {
        val payload = "WIFI:T:WPA;S:test;P:secret;;"
        val bmp = qrBitmap(payload)
        val w = bmp.width
        val h = bmp.height
        val pixels = IntArray(w * h)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)
        // Pack luma with padded row stride like CameraX YUV planes.
        val stride = w + 16
        val y = ByteArray(stride * h)
        for (row in 0 until h) {
            for (col in 0 until w) {
                val c = pixels[row * w + col]
                val r = (c shr 16) and 0xff
                val g = (c shr 8) and 0xff
                val b = c and 0xff
                y[row * stride + col] = ((r * 77 + g * 150 + b * 29) shr 8).toByte()
            }
        }
        val decoded = QrDecoder.decodeYPlane(java.nio.ByteBuffer.wrap(y), w, h, stride, 0)
        assertEquals(payload, decoded)
        val rotated90 = QrDecoder.decodeYPlane(java.nio.ByteBuffer.wrap(y), w, h, stride, 90)
        assertEquals(payload, rotated90)
    }

    @Test
    fun sampleSizeDownscalesPhonePhotos() {
        assertEquals(1, QrDecoder.sampleSize(800, 600, 1600))
        assertEquals(2, QrDecoder.sampleSize(3200, 2400, 1600))
        assertEquals(4, QrDecoder.sampleSize(6400, 4800, 1600))
    }

    @Test
    fun decodePhotoFromJpegBytes() {
        val payload = "https://example.com/photo-scan"
        val bmp = qrBitmap(payload)
        val stream = java.io.ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        val decoded = QrDecoder.decodePhoto(java.io.ByteArrayInputStream(stream.toByteArray()))
        assertEquals(payload, decoded)
    }

    private fun qrBitmap(text: String): Bitmap {
        val matrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 256, 256)
        val bmp = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
        for (y in 0 until matrix.height) {
            for (x in 0 until matrix.width) {
                bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        assertTrue(bmp.width > 0)
        return bmp
    }
}
