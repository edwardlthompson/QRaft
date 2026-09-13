package org.qraft.render

import org.qraft.coreqr.Scannability

enum class ModuleShape { SQUARE, ROUNDED, DOT, DIAMOND, PILL_H, PILL_V, BLOB }

enum class FinderShape { SQUARE, ROUNDED, CIRCLE, HEX, RING }

enum class QrFrame { NONE, THIN, BOLD, ROUNDED }

data class LogoCutout(
    val enabled: Boolean = false,
    val diameterFraction: Double = 0.20,
) {
    val clampedFraction: Double
        get() = diameterFraction.coerceIn(MIN_FRACTION, MAX_FRACTION)

    companion object {
        const val MIN_FRACTION = 0.10
        const val MAX_FRACTION = 0.28
    }
}

data class QrStyle(
    val moduleShape: ModuleShape = ModuleShape.SQUARE,
    val finderShape: FinderShape = FinderShape.SQUARE,
    val finderPupil: FinderShape = FinderShape.SQUARE,
    val foregroundArgb: Int = 0xFF000000.toInt(),
    val eyeColorArgb: Int = 0xFF000000.toInt(),
    val backgroundArgb: Int = 0xFFFFFFFF.toInt(),
    val quietZoneModules: Int = Scannability.MIN_QUIET_ZONE_MODULES,
    val logoCutout: LogoCutout = LogoCutout(),
    val colorTheme: ColorTheme = ColorTheme.CLASSIC,
    val gradient: GradientSpec = GradientSpec(),
    val centerMark: CenterMark = CenterMark.NONE,
    val caption: CaptionSpec = CaptionSpec(),
    val imageBackgroundPath: String = "",
    val logoImagePath: String = "",
    val frame: QrFrame = QrFrame.NONE,
    val cornerBadge: Boolean = false,
) {
    val hasOverlay: Boolean
        get() = logoCutout.enabled ||
            (centerMark != CenterMark.NONE) ||
            cornerBadge ||
            logoImagePath.isNotBlank()

    companion object {
        val DEFAULT = QrStyle()
    }
}
