package org.qraft.coreqr

/**
 * Deterministic scannability helpers. Warnings are advisory — encoding still succeeds.
 */
object Scannability {
    const val MIN_QUIET_ZONE_MODULES = 4
    const val MAX_LOGO_COVERAGE_H = 0.20
    const val MAX_LOGO_COVERAGE_Q = 0.12
    const val MAX_LOGO_COVERAGE_M = 0.08
    const val MAX_LOGO_COVERAGE_L = 0.05
    const val MIN_CONTRAST_RATIO = 3.0

    data class Report(
        val warnings: List<Warning>,
    ) {
        val isOk: Boolean get() = warnings.isEmpty()
    }

    enum class Warning {
        LOW_CONTRAST,
        LOGO_TOO_LARGE,
        QUIET_ZONE_TOO_SMALL,
        OVERLAY_NEEDS_HIGH_ECC,
    }

    /** WCAG-style contrast ratio for opaque sRGB colors (ARGB ints). */
    fun contrastRatio(foregroundArgb: Int, backgroundArgb: Int): Double {
        val l1 = relativeLuminance(foregroundArgb)
        val l2 = relativeLuminance(backgroundArgb)
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    fun relativeLuminance(argb: Int): Double {
        fun channel(c: Int): Double {
            val s = c / 255.0
            return if (s <= 0.03928) s / 12.92 else Math.pow((s + 0.055) / 1.055, 2.4)
        }
        val r = channel((argb shr 16) and 0xFF)
        val g = channel((argb shr 8) and 0xFF)
        val b = channel(argb and 0xFF)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    fun maxLogoCoverage(ecc: ErrorCorrectionLevel): Double = when (ecc) {
        ErrorCorrectionLevel.H -> MAX_LOGO_COVERAGE_H
        ErrorCorrectionLevel.Q -> MAX_LOGO_COVERAGE_Q
        ErrorCorrectionLevel.M -> MAX_LOGO_COVERAGE_M
        ErrorCorrectionLevel.L -> MAX_LOGO_COVERAGE_L
    }

    fun analyze(
        matrix: QrMatrix,
        foregroundArgb: Int = 0xFF000000.toInt(),
        backgroundArgb: Int = 0xFFFFFFFF.toInt(),
        quietZoneModules: Int = MIN_QUIET_ZONE_MODULES,
        logoCoverage: Double = 0.0,
        hasOverlay: Boolean = false,
    ): Report {
        val warnings = mutableListOf<Warning>()
        if (contrastRatio(foregroundArgb, backgroundArgb) < MIN_CONTRAST_RATIO) {
            warnings += Warning.LOW_CONTRAST
        }
        if (quietZoneModules < MIN_QUIET_ZONE_MODULES) {
            warnings += Warning.QUIET_ZONE_TOO_SMALL
        }
        if (logoCoverage > maxLogoCoverage(matrix.errorCorrection)) {
            warnings += Warning.LOGO_TOO_LARGE
        }
        if (hasOverlay && matrix.errorCorrection != ErrorCorrectionLevel.H) {
            warnings += Warning.OVERLAY_NEEDS_HIGH_ECC
        }
        return Report(warnings)
    }
}
