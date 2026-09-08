package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SquareRasterizerTest {
    @Test
    fun doesNotStretchModules() {
        val matrix = QrEncoder.encodeText("https://example.com")
        val result = SquareRasterizer.rasterize(matrix, sizePx = 512)
        val modulesWithQuiet = matrix.size + 8
        assertEquals(512 / modulesWithQuiet, result.modulePx)
        assertEquals(result.modulePx * modulesWithQuiet, result.contentSizePx)
        assertTrue(result.contentSizePx <= 512)
    }

    @Test
    fun finderCornerIsDark() {
        val matrix = QrEncoder.encodeText("corner")
        val result = SquareRasterizer.rasterize(matrix, sizePx = 256)
        val x = result.contentOriginX + 4 * result.modulePx + result.modulePx / 2
        val y = result.contentOriginY + 4 * result.modulePx + result.modulePx / 2
        assertEquals(0xFF000000.toInt(), result.pixels[y * result.width + x])
    }

    @Test
    fun oledBackgroundIsTrueBlack() {
        val matrix = QrEncoder.encodeText("oled")
        val result = SquareRasterizer.rasterize(
            matrix,
            sizePx = 128,
            options = SquareRasterizer.Options(backgroundArgb = 0xFF000000.toInt()),
        )
        assertEquals(0xFF000000.toInt(), result.pixels[0])
    }

    @Test
    fun rasterIsDeterministic() {
        val matrix = QrEncoder.encodeText("same")
        val a = SquareRasterizer.rasterize(matrix, 200)
        val b = SquareRasterizer.rasterize(matrix, 200)
        assertEquals(a, b)
    }
}
