package org.qraft.coreqr

enum class QrSurface { EDITOR, WIDGET, WALLPAPER, EXPORT }

object EccPolicy {
    fun choose(surface: QrSurface, hasOverlay: Boolean): ErrorCorrectionLevel {
        if (hasOverlay) return ErrorCorrectionLevel.H
        return when (surface) {
            QrSurface.WALLPAPER -> ErrorCorrectionLevel.H
            QrSurface.WIDGET -> ErrorCorrectionLevel.M
            QrSurface.EDITOR, QrSurface.EXPORT -> ErrorCorrectionLevel.M
        }
    }

    fun forceHighEcc(hasOverlay: Boolean): Boolean = hasOverlay

    fun boostEcl(surface: QrSurface, hasOverlay: Boolean): Boolean =
        !hasOverlay && surface != QrSurface.WALLPAPER
}
