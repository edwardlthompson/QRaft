package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.coreqr.QrEncoder
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class QrExportTest {
    @Test
    fun jsonRoundTrip() {
        val doc = QrExportDocument(
            payloadText = "https://example.com",
            style = QrStyle(moduleShape = ModuleShape.DOT, foregroundArgb = 0xFF112233.toInt()),
        )
        val again = QrExportJson.decode(QrExportJson.encode(doc))
        assertEquals(doc, again)
    }

    @Test
    fun galleryDocumentRoundTripsThemeAndCaption() {
        val doc = QrExportDocument(
            payloadText = "WIFI:T:WPA;S:Home;P:secret;;",
            style = QrStyle(
                colorTheme = ColorTheme.SUNSET,
                caption = CaptionSpec("Scan"),
                centerMark = CenterMark.WIFI,
            ),
        )
        val again = QrExportJson.decode(QrExportJson.encode(doc))
        assertEquals(doc, again)
        assertEquals(ColorTheme.SUNSET, again?.style?.colorTheme)
        assertEquals("Scan", again?.style?.caption?.text)
    }

    @Test
    fun invalidJsonIsNull() {
        assertNull(QrExportJson.decode(""))
        assertNull(QrExportJson.decode("not-json"))
        assertNull(QrExportJson.decode("{\"version\":1}"))
    }

    @Test
    fun svgHasNamespaceAndQuietZoneSize() {
        val matrix = QrEncoder.encodeText("svg")
        val svg = QrSvgExporter.export(matrix, QrStyle.DEFAULT, modulePx = 4)
        val dim = (matrix.size + 8) * 4
        assertTrue(svg.startsWith("<svg xmlns=\"http://www.w3.org/2000/svg\""))
        assertTrue(svg.contains("width=\"$dim\""))
        assertTrue(svg.contains("fill=\"#000000\""))
        assertTrue(svg.endsWith("</svg>"))
        assertNotNull(QrSvgExporter.hexRgb(0xFF00FF00.toInt()))
        assertEquals("#00FF00", QrSvgExporter.hexRgb(0xFF00FF00.toInt()))
    }

    @Test
    fun styledSvgUsesCirclesForDots() {
        val matrix = QrEncoder.encodeText("svg-dot")
        val svg = QrSvgExporter.export(matrix, QrStyle(moduleShape = ModuleShape.DOT), modulePx = 8)
        assertTrue(svg.contains("<circle "))
        val diamond = QrSvgExporter.export(matrix, QrStyle(moduleShape = ModuleShape.DIAMOND), modulePx = 8)
        assertTrue(diamond.contains("<polygon "))
    }

    @Test
    fun styleJsonQrPayloadRoundTrips() {
        val style = QrStyle(moduleShape = ModuleShape.DIAMOND)
        val payload = StyleJsonQr.payload(style)
        assertEquals(style, QrStyleJson.decode(payload))
        assertNotNull(QrEncoder.encodeText(payload))
    }
}
