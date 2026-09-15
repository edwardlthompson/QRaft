package org.qraft.render

import kotlin.math.hypot

/** Paint Streamline SVG bodies onto a bitmap (fill + round stroke). */
internal object SvgIconRaster {
    fun blit(pixels: IntArray, size: Int, svg: String, pad: Float) {
        val vb = viewBox(svg)
        val scale = (size - 2f * pad) / maxOf(vb, 1f)
        val paths = elements(svg)
        for (p in paths) {
            val contours = SvgPath.contours(p.d).map { c -> c.map { Pt(it.x * scale + pad, it.y * scale + pad) } }
            if (p.fill != 0) fill(pixels, size, contours, p.fill)
            if (p.stroke != 0) stroke(pixels, size, contours, p.stroke, (p.strokeWidth * scale).coerceAtLeast(1f))
        }
    }

    fun nested(svg: String, x: Double, y: Double, w: Double, h: Double): String {
        val vb = viewBox(svg).toInt().coerceAtLeast(24)
        val inner = svg.substringAfter('>').substringBeforeLast("</svg>")
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" x=\"$x\" y=\"$y\" width=\"$w\" height=\"$h\" viewBox=\"0 0 $vb $vb\">$inner</svg>"
    }

    private data class El(val d: String, val fill: Int, val stroke: Int, val strokeWidth: Float)

    private fun viewBox(svg: String): Float {
        val m = Regex("""viewBox="0 0 ([0-9.]+)""").find(svg) ?: return 24f
        return m.groupValues[1].toFloat()
    }

    private fun elements(svg: String): List<El> {
        val defs = HashMap<String, String>()
        val idRe = Regex("""<path[^>]*id="([^"]+)"[^>]*>""")
        for (m in idRe.findAll(svg)) defs[m.groupValues[1]] = m.value
        val defsStart = svg.indexOf("<defs")
        val defsEnd = svg.indexOf("</defs>")
        val out = ArrayList<El>()
        val tagRe = Regex("""<(path|use)\b([^>]*)/?>""")
        for (m in tagRe.findAll(svg)) {
            val inDefs = defsStart >= 0 && m.range.first >= defsStart && (defsEnd < 0 || m.range.first < defsEnd)
            val rawAttrs = m.groupValues[2]
            val attrs = if (m.groupValues[1] == "use") {
                val href = Regex("""href="#([^"]+)"""").find(rawAttrs)?.groupValues?.get(1) ?: continue
                val defTag = defs[href] ?: continue
                defTag + " " + rawAttrs
            } else rawAttrs
            if (inDefs || attrs.contains("clipPath") || attrs.contains("clip-path")) continue
            val d = Regex("""\bd="([^"]+)"""").find(attrs)?.groupValues?.get(1) ?: continue
            val fill = color(attr(attrs, "fill"), 0)
            val stroke = color(attr(attrs, "stroke"), 0)
            val sw = attr(attrs, "stroke-width")?.toFloatOrNull() ?: 1f
            if (fill != 0 || stroke != 0) out.add(El(d, fill, stroke, sw))
        }
        return out
    }

    private fun attr(attrs: String, name: String): String? =
        Regex("""$name="([^"]+)"""").find(attrs)?.groupValues?.get(1)

    private fun color(raw: String?, fallback: Int): Int {
        if (raw == null || raw == "none") return fallback
        val hex = raw.removePrefix("#")
        if (hex.length != 6) return fallback
        return (0xFF shl 24) or hex.toInt(16)
    }

    private fun fill(pixels: IntArray, size: Int, contours: List<List<Pt>>, color: Int) {
        for (y in 0 until size) {
            val py = y + 0.5f
            val row = y * size
            for (x in 0 until size) if (inside(x + 0.5f, py, contours)) pixels[row + x] = color
        }
    }

    private fun stroke(pixels: IntArray, size: Int, contours: List<List<Pt>>, color: Int, width: Float) {
        val r = width / 2f
        val r2 = r * r
        fun stamp(px: Float, py: Float) {
            val x0 = maxOf(0, (px - r).toInt())
            val y0 = maxOf(0, (py - r).toInt())
            val x1 = minOf(size, (px + r).toInt() + 1)
            val y1 = minOf(size, (py + r).toInt() + 1)
            for (y in y0 until y1) {
                val row = y * size
                val dy = y + 0.5f - py
                for (x in x0 until x1) {
                    val dx = x + 0.5f - px
                    if (dx * dx + dy * dy <= r2) pixels[row + x] = color
                }
            }
        }
        for (c in contours) {
            if (c.isEmpty()) continue
            for (i in 0 until c.lastIndex) {
                val a = c[i]; val b = c[i + 1]
                val dist = hypot((b.x - a.x).toDouble(), (b.y - a.y).toDouble()).toFloat()
                val n = maxOf(1, (dist / 0.6f).toInt())
                for (s in 0..n) {
                    val t = s / n.toFloat()
                    stamp(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)
                }
            }
        }
    }

    private fun inside(px: Float, py: Float, contours: List<List<Pt>>): Boolean {
        var winding = 0
        for (c in contours) {
            if (c.size < 2) continue
            for (i in 0 until c.lastIndex) {
                val a = c[i]; val b = c[i + 1]
                if ((a.y > py) == (b.y > py)) continue
                val ix = a.x + (py - a.y) / (b.y - a.y) * (b.x - a.x)
                if (px < ix) winding += if (a.y < b.y) 1 else -1
            }
        }
        return winding != 0
    }
}
