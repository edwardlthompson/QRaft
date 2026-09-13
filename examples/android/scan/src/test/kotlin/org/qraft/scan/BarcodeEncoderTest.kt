package org.qraft.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BarcodeEncoderTest {
    @Test
    fun encodesCode128() {
        val matrix = BarcodeEncoder.encode(Barcode1dKind.Code128, "QRAFT-128")
        assertNotNull(matrix)
        assertEquals(true, matrix!!.width > 40)
        val pixels = BarcodeEncoder.toArgb(matrix)
        assertEquals(matrix.width * matrix.height, pixels.size)
    }

    @Test
    fun rejectsBadEan13() {
        assertNull(BarcodeEncoder.encode(Barcode1dKind.Ean13, "abc"))
        assertNull(BarcodeEncoder.encode(Barcode1dKind.Ean13, "123"))
    }

    @Test
    fun encodesEan13Digits() {
        val matrix = BarcodeEncoder.encode(Barcode1dKind.Ean13, "5901234123457")
        assertNotNull(matrix)
    }
}
