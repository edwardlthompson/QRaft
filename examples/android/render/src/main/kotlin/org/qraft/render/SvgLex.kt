package org.qraft.render

internal object SvgLex {
    fun skip(d: String, start: Int): Int {
        var i = start
        while (i < d.length && d[i] in ", \n\t\r") i++
        return i
    }

    fun number(d: String, start: Int): Pair<Float, Int>? {
        var i = start
        if (i >= d.length) return null
        val n = StringBuilder()
        if (d[i] == '+' || d[i] == '-') n.append(d[i++])
        var dot = false
        while (i < d.length) {
            val c = d[i]
            if (c in '0'..'9') n.append(c)
            else if (c == '.' && !dot) { dot = true; n.append(c) }
            else if ((c == 'e' || c == 'E') && n.isNotEmpty()) {
                n.append(c); i++
                if (i < d.length && (d[i] == '+' || d[i] == '-')) n.append(d[i++])
                continue
            } else break
            i++
        }
        return n.toString().toFloatOrNull()?.let { it to i }
    }
}
