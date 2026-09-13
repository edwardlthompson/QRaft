package org.qraft.wallpaper

import org.qraft.render.QrStyle

/** Dark/light wallpaper pair: invert body colors for the lock (or second) surface. */
object WallpaperPair {
    fun darkVariant(style: QrStyle): QrStyle = style.copy(
        foregroundArgb = style.backgroundArgb,
        backgroundArgb = style.foregroundArgb,
        eyeColorArgb = style.backgroundArgb,
    )

    fun previewLabel(home: Boolean): String = if (home) "home" else "lock"
}
