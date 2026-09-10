package org.qraft.render

import org.qraft.coreqr.QrMatrix

object QrSvgExporter {
    fun export(matrix: QrMatrix, style: QrStyle = QrStyle.DEFAULT, modulePx: Int = 8): String {
        require(modulePx > 0)
        val quiet = style.quietZoneModules.coerceAtLeast(0)
        val dim = (matrix.size + 2 * quiet) * modulePx
        val bg = hexRgb(style.backgroundArgb)
        val fg = hexRgb(style.foregroundArgb)
        val out = StringBuilder(256 + matrix.size * matrix.size * 48)
        out.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
            .append(dim).append("\" height=\"").append(dim)
            .append("\" viewBox=\"0 0 ").append(dim).append(' ').append(dim).append("\">")
        out.append("<rect width=\"").append(dim).append("\" height=\"").append(dim)
            .append("\" fill=\"").append(bg).append("\"/>")
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size) {
                if (!matrix.isDark(x, y)) continue
                val left = (x + quiet) * modulePx
                val top = (y + quiet) * modulePx
                appendModule(out, style.moduleShape, left, top, modulePx, fg)
            }
        }
        out.append("</svg>")
        return out.toString()
    }

    private fun appendModule(
        out: StringBuilder,
        shape: ModuleShape,
        left: Int,
        top: Int,
        modulePx: Int,
        fg: String,
    ) {
        val cx = left + modulePx / 2.0
        val cy = top + modulePx / 2.0
        when (shape) {
            ModuleShape.DOT -> out.append("<circle cx=\"").append(cx).append("\" cy=\"").append(cy)
                .append("\" r=\"").append(modulePx * 0.42).append("\" fill=\"").append(fg).append("\"/>")
            ModuleShape.ROUNDED, ModuleShape.BLOB, ModuleShape.PILL_H, ModuleShape.PILL_V ->
                out.append("<rect x=\"").append(left).append("\" y=\"").append(top)
                    .append("\" width=\"").append(modulePx).append("\" height=\"").append(modulePx)
                    .append("\" rx=\"").append(modulePx / 3).append("\" fill=\"").append(fg).append("\"/>")
            ModuleShape.DIAMOND -> out.append("<polygon points=\"")
                .append(cx).append(',').append(top).append(' ')
                .append(left + modulePx).append(',').append(cy).append(' ')
                .append(cx).append(',').append(top + modulePx).append(' ')
                .append(left).append(',').append(cy)
                .append("\" fill=\"").append(fg).append("\"/>")
            ModuleShape.SQUARE -> out.append("<rect x=\"").append(left).append("\" y=\"").append(top)
                .append("\" width=\"").append(modulePx).append("\" height=\"").append(modulePx)
                .append("\" fill=\"").append(fg).append("\"/>")
        }
    }

    internal fun hexRgb(argb: Int): String {
        val rgb = argb and 0xFFFFFF
        return "#%06X".format(rgb)
    }
}
