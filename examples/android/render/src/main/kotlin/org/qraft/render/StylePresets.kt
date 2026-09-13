package org.qraft.render

/** Curated offline style packs (no network). */
object StylePresets {
    data class Pack(val id: String, val label: String, val style: QrStyle)

    val ALL: List<Pack> = listOf(
        Pack("classic", "Classic", QrStyle.DEFAULT),
        Pack(
            "ink",
            "Ink",
            QrStyle(
                moduleShape = ModuleShape.ROUNDED,
                finderShape = FinderShape.ROUNDED,
                finderPupil = FinderShape.ROUNDED,
                foregroundArgb = 0xFF111111.toInt(),
                eyeColorArgb = 0xFF111111.toInt(),
                backgroundArgb = 0xFFFAFAFA.toInt(),
                frame = QrFrame.THIN,
            ),
        ),
        Pack(
            "ocean",
            "Ocean",
            ColorThemes.apply(
                ColorTheme.BLUE,
                QrStyle(moduleShape = ModuleShape.DOT, finderShape = FinderShape.CIRCLE),
            ),
        ),
        Pack(
            "forest",
            "Forest",
            ColorThemes.apply(
                ColorTheme.FOREST,
                QrStyle(moduleShape = ModuleShape.ROUNDED, frame = QrFrame.ROUNDED),
            ),
        ),
        Pack(
            "oled",
            "OLED",
            ColorThemes.apply(ColorTheme.OLED, QrStyle(moduleShape = ModuleShape.SQUARE)),
        ),
        Pack(
            "sunset",
            "Sunset",
            ColorThemes.apply(
                ColorTheme.SUNSET,
                QrStyle(moduleShape = ModuleShape.ROUNDED, finderShape = FinderShape.ROUNDED),
            ),
        ),
    )

    fun byId(id: String): Pack? = ALL.find { it.id == id }
}
