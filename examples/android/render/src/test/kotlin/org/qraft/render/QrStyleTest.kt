package org.qraft.render

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.coreqr.ErrorCorrectionLevel
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrPayload
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class QrStyleJsonTest {
    @Test
    fun blankJsonIsDefault() {
        assertEquals(QrStyle.DEFAULT, QrStyleJson.decode(""))
        assertEquals(QrStyle.DEFAULT, QrStyleJson.decode("{}"))
        assertEquals(QrStyle.DEFAULT, QrStyleJson.decode("not-json"))
    }

    @Test
    fun roundTripPreservesFields() {
        val style = QrStyle(
            moduleShape = ModuleShape.DOT,
            finderShape = FinderShape.ROUNDED,
            finderPupil = FinderShape.CIRCLE,
            foregroundArgb = 0xFF112233.toInt(),
            eyeColorArgb = 0xFF445566.toInt(),
            backgroundArgb = 0xFFFAFAFA.toInt(),
            quietZoneModules = 4,
            logoCutout = LogoCutout(enabled = true, diameterFraction = 0.22),
            colorTheme = ColorTheme.FOREST,
            caption = CaptionSpec("Scan"),
            centerMark = CenterMark.LINK,
        )
        assertEquals(style, QrStyleJson.decode(QrStyleJson.encode(style)))
        assertTrue(JSONObject(QrStyleJson.encode(style)).getBoolean("logoEnabled"))
    }

    @Test
    fun unknownEnumFallsBack() {
        val json = """{"moduleShape":"STAR","finderShape":"STAR"}"""
        val style = QrStyleJson.decode(json)
        assertEquals(ModuleShape.SQUARE, style.moduleShape)
        assertEquals(FinderShape.SQUARE, style.finderShape)
        assertTrue(QrStyle(logoCutout = LogoCutout(enabled = true)).hasOverlay)
    }
}

class StyledQrRasterizerTest {
    @Test
    fun defaultStyleMatchesSquareRasterizer() {
        val matrix = QrEncoder.encodeText("https://example.com")
        val styled = StyledQrRasterizer.rasterize(matrix, 256)
        val square = org.qraft.coreqr.SquareRasterizer.rasterize(matrix, 256)
        assertEquals(square, styled)
    }

    @Test
    fun logoCutoutPaintsBackgroundAtCenter() {
        val matrix = QrEncoder.encode(
            QrPayload.Text("cutout"),
            errorCorrection = ErrorCorrectionLevel.H,
            forceHighEcc = true,
        )
        val bg = 0xFFEEEEEE.toInt()
        val result = StyledQrRasterizer.rasterize(
            matrix,
            256,
            QrStyle(
                foregroundArgb = 0xFF000000.toInt(),
                backgroundArgb = bg,
                logoCutout = LogoCutout(enabled = true, diameterFraction = 0.20),
            ),
        )
        val cx = result.contentOriginX + result.contentSizePx / 2
        val cy = result.contentOriginY + result.contentSizePx / 2
        assertEquals(bg, result.pixels[cy * result.width + cx])
    }

    @Test
    fun roundedIsDeterministic() {
        val matrix = QrEncoder.encodeText("round")
        val style = QrStyle(moduleShape = ModuleShape.ROUNDED, finderShape = FinderShape.ROUNDED)
        assertEquals(
            StyledQrRasterizer.rasterize(matrix, 200, style),
            StyledQrRasterizer.rasterize(matrix, 200, style),
        )
    }

    @Test
    fun diamondOccupiesDarkModuleCenters() {
        val matrix = QrEncoder.encodeText("diamond")
        val style = QrStyle(moduleShape = ModuleShape.DIAMOND)
        val result = StyledQrRasterizer.rasterize(matrix, 256, style)
        val quiet = style.quietZoneModules
        var checked = 0
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (!matrix.isDark(x, y)) continue
                val cx = result.contentOriginX + (x + quiet) * result.modulePx + result.modulePx / 2
                val cy = result.contentOriginY + (y + quiet) * result.modulePx + result.modulePx / 2
                if (cx in 0 until result.width && cy in 0 until result.height) {
                    assertEquals(style.foregroundArgb, result.pixels[cy * result.width + cx])
                    checked++
                }
            }
        }
        assertTrue(checked > 10)
    }

    @Test
    fun logoFractionClamps() {
        assertEquals(0.10, LogoCutout(diameterFraction = 0.01).clampedFraction, 0.0)
        assertEquals(0.28, LogoCutout(diameterFraction = 0.9).clampedFraction, 0.0)
        assertFalse(QrStyle.DEFAULT.logoCutout.enabled)
        assertFalse(QrStyle.DEFAULT.hasOverlay)
    }

    @Test
    fun lightModuleCentersStayBackground() {
        val matrix = QrEncoder.encodeText("light-cells")
        val style = QrStyle(moduleShape = ModuleShape.PILL_H, backgroundArgb = 0xFFEEEEEE.toInt())
        val result = StyledQrRasterizer.rasterize(matrix, 256, style)
        val quiet = style.quietZoneModules
        var checked = 0
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (matrix.isDark(x, y)) continue
                val cx = result.contentOriginX + (x + quiet) * result.modulePx + result.modulePx / 2
                val cy = result.contentOriginY + (y + quiet) * result.modulePx + result.modulePx / 2
                if (cx in 0 until result.width && cy in 0 until result.height) {
                    assertEquals(style.backgroundArgb, result.pixels[cy * result.width + cx])
                    checked++
                }
            }
        }
        assertTrue(checked > 10)
    }

}
