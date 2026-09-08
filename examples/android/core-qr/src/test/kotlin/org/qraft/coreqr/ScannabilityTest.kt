package org.qraft.coreqr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScannabilityTest {
    @Test
    fun blackOnWhiteHasHighContrast() {
        val ratio = Scannability.contrastRatio(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
        assertTrue(ratio > 20.0)
    }

    @Test
    fun lowContrastWarns() {
        val matrix = QrEncoder.encodeText("x")
        val report = Scannability.analyze(
            matrix,
            foregroundArgb = 0xFF888888.toInt(),
            backgroundArgb = 0xFF999999.toInt(),
        )
        assertTrue(report.warnings.contains(Scannability.Warning.LOW_CONTRAST))
    }

    @Test
    fun quietZoneWarnsWhenTooSmall() {
        val matrix = QrEncoder.encodeText("quiet")
        val report = Scannability.analyze(matrix, quietZoneModules = 2)
        assertTrue(report.warnings.contains(Scannability.Warning.QUIET_ZONE_TOO_SMALL))
    }

    @Test
    fun overlayWithoutHWarns() {
        val matrix = QrEncoder.encodeText("logo", ErrorCorrectionLevel.M, boostEcl = false)
        val report = Scannability.analyze(matrix, hasOverlay = true)
        assertTrue(report.warnings.contains(Scannability.Warning.OVERLAY_NEEDS_HIGH_ECC))
    }

    @Test
    fun logoCoverageLimitDependsOnEcc() {
        assertEquals(0.20, Scannability.maxLogoCoverage(ErrorCorrectionLevel.H), 0.0)
        assertEquals(0.05, Scannability.maxLogoCoverage(ErrorCorrectionLevel.L), 0.0)
    }

    @Test
    fun cleanBlackWhiteIsOk() {
        val matrix = QrEncoder.encodeText("ok")
        val report = Scannability.analyze(matrix)
        assertTrue(report.isOk)
        assertFalse(report.warnings.contains(Scannability.Warning.LOW_CONTRAST))
    }
}
