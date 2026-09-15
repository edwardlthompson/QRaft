package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.coreqr.QrEncoder
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class QrRasterExtrasTest {
    @Test
    fun imageBackgroundIsDimmedInQuietZone() {
        val matrix = QrEncoder.encodeText("bg")
        val size = 128
        val raw = 0xFF0000FF.toInt()
        val extras = RasterExtras(bgPixels = IntArray(size * size) { raw })
        val result = StyledQrRasterizer.rasterize(matrix, size, QrStyle.DEFAULT, extras)
        assertNotEquals(raw, result.pixels[0])
        assertEquals(result.width, result.height)
    }

    @Test
    fun missingInlaySkipsBlit() {
        val matrix = QrEncoder.encodeText("inlay")
        val style = QrStyle(logoCutout = LogoCutout(enabled = true))
        val result = StyledQrRasterizer.rasterize(matrix, 96, style, RasterExtras())
        assertEquals(96, result.width)
        assertEquals(96, result.height)
    }

    @Test
    fun centerMarkBakesWithoutExtras() {
        val matrix = QrEncoder.encodeText("https://example.com")
        val style = QrStyle(centerMark = CenterMark.WIFI, logoCutout = LogoCutout(enabled = true))
        val result = StyledQrRasterizer.rasterize(matrix, 256, style)
        val cx = result.contentOriginX + result.contentSizePx / 2
        val cy = result.contentOriginY + result.contentSizePx / 2
        val cyan = 0xFFC2F3FF.toInt()
        val gold = 0xFFFFEF5E.toInt()
        assertTrue(result.pixels.contains(cyan) || result.pixels.contains(gold))
        assertTrue(result.pixels.contains(CenterMarkIcons.WHITE))
        assertNotEquals(style.foregroundArgb, result.pixels[cy * result.width + cx])
        val wifi = CenterMarkIcons.bitmap(CenterMark.WIFI, 64)
        assertTrue(wifi.first.contains(CenterMarkIcons.WHITE))
        assertTrue(wifi.first.contains(cyan) || wifi.first.contains(gold))
    }

    @Test
    fun cryptoPlateUsesBitcoinGold() {
        val bmp = CenterMarkIcons.bitmap(CenterMark.CRYPTO, 96)
        assertTrue(bmp.first.contains(0xFFFFEF5E.toInt()))
        assertTrue(bmp.first.contains(CenterMarkIcons.WHITE))
        assertTrue(CenterMarkIcons.svg(CenterMark.WIFI).contains("viewBox"))
    }

    @Test
    fun autoMarkFollowsWifiPayload() {
        assertEquals(CenterMark.WIFI, CenterMarkResolve.of(CenterMark.AUTO, "WIFI:T:WPA;S:Home;P:x;;"))
        assertEquals(CenterMark.LINK, CenterMarkResolve.of(CenterMark.AUTO, "https://qraft.app"))
    }
}
