package org.qraft.render

import kotlin.math.abs

internal data class Pt(val x: Float, val y: Float)

internal object SvgPath {
    fun contours(d: String): List<List<Pt>> {
        val out = ArrayList<List<Pt>>()
        val cur = ArrayList<Pt>()
        val nums = ArrayList<Float>()
        var i = 0
        var cmd = 'M'
        var x = 0f
        var y = 0f
        var sx = 0f
        var sy = 0f
        var cx = 0f
        var cy = 0f
        var prevC = false
        fun emit(px: Float, py: Float) { cur.add(Pt(px, py)) }
        fun close() { if (cur.size >= 2) out.add(ArrayList(cur)); cur.clear() }
        fun take(n: Int): Boolean {
            nums.clear()
            repeat(n) {
                i = SvgLex.skip(d, i)
                if (i >= d.length) return false
                val (v, ni) = SvgLex.number(d, i) ?: return false
                nums.add(v)
                i = ni
            }
            return true
        }
        while (i < d.length) {
            i = SvgLex.skip(d, i)
            if (i >= d.length) break
            val c = d[i]
            if (c in 'A'..'Z' || c in 'a'..'z') {
                cmd = c
                i++
            }
            val rel = cmd in 'a'..'z'
            val op = cmd.lowercaseChar()
            when (op) {
                'm' -> {
                    if (!take(2)) break
                    if (rel) { x += nums[0]; y += nums[1] } else { x = nums[0]; y = nums[1] }
                    close(); sx = x; sy = y; emit(x, y); cmd = if (rel) 'l' else 'L'; prevC = false
                }
                'l' -> {
                    if (!take(2)) break
                    if (rel) { x += nums[0]; y += nums[1] } else { x = nums[0]; y = nums[1] }
                    emit(x, y); prevC = false
                }
                'h' -> {
                    if (!take(1)) break
                    x = if (rel) x + nums[0] else nums[0]
                    emit(x, y); prevC = false
                }
                'v' -> {
                    if (!take(1)) break
                    y = if (rel) y + nums[0] else nums[0]
                    emit(x, y); prevC = false
                }
                'c' -> {
                    if (!take(6)) break
                    val x1 = if (rel) x + nums[0] else nums[0]
                    val y1 = if (rel) y + nums[1] else nums[1]
                    val x2 = if (rel) x + nums[2] else nums[2]
                    val y2 = if (rel) y + nums[3] else nums[3]
                    val x3 = if (rel) x + nums[4] else nums[4]
                    val y3 = if (rel) y + nums[5] else nums[5]
                    cubic(x, y, x1, y1, x2, y2, x3, y3, ::emit)
                    cx = x2; cy = y2; x = x3; y = y3; prevC = true
                }
                's' -> {
                    if (!take(4)) break
                    val x1 = if (prevC) 2 * x - cx else x
                    val y1 = if (prevC) 2 * y - cy else y
                    val x2 = if (rel) x + nums[0] else nums[0]
                    val y2 = if (rel) y + nums[1] else nums[1]
                    val x3 = if (rel) x + nums[2] else nums[2]
                    val y3 = if (rel) y + nums[3] else nums[3]
                    cubic(x, y, x1, y1, x2, y2, x3, y3, ::emit)
                    cx = x2; cy = y2; x = x3; y = y3; prevC = true
                }
                'q' -> {
                    if (!take(4)) break
                    val x1 = if (rel) x + nums[0] else nums[0]
                    val y1 = if (rel) y + nums[1] else nums[1]
                    val x2 = if (rel) x + nums[2] else nums[2]
                    val y2 = if (rel) y + nums[3] else nums[3]
                    cubic(x, y, (x + 2 * x1) / 3f, (y + 2 * y1) / 3f, (x2 + 2 * x1) / 3f, (y2 + 2 * y1) / 3f, x2, y2, ::emit)
                    cx = x1; cy = y1; x = x2; y = y2; prevC = true
                }
                'a' -> {
                    if (!take(7)) break
                    val nx = if (rel) x + nums[5] else nums[5]
                    val ny = if (rel) y + nums[6] else nums[6]
                    SvgArc.append(x, y, nums[0], nums[1], nums[2], nums[3] != 0f, nums[4] != 0f, nx, ny, ::emit)
                    x = nx; y = ny; prevC = false
                }
                'z' -> { emit(sx, sy); close(); x = sx; y = sy; prevC = false }
                else -> i++
            }
        }
        close()
        return out
    }

    private fun cubic(
        x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float,
        emit: (Float, Float) -> Unit, depth: Int = 0,
    ) {
        val dx = x3 - x0
        val dy = y3 - y0
        val d = abs((x1 - x3) * dy - (y1 - y3) * dx) + abs((x2 - x3) * dy - (y2 - y3) * dx)
        if (d < 0.35f || depth > 8) { emit(x3, y3); return }
        val ax = (x0 + x1) / 2f; val ay = (y0 + y1) / 2f
        val bx = (x1 + x2) / 2f; val by = (y1 + y2) / 2f
        val cx = (x2 + x3) / 2f; val cy = (y2 + y3) / 2f
        val dx1 = (ax + bx) / 2f; val dy1 = (ay + by) / 2f
        val dx2 = (bx + cx) / 2f; val dy2 = (by + cy) / 2f
        val mx = (dx1 + dx2) / 2f; val my = (dy1 + dy2) / 2f
        cubic(x0, y0, ax, ay, dx1, dy1, mx, my, emit, depth + 1)
        cubic(mx, my, dx2, dy2, cx, cy, x3, y3, emit, depth + 1)
    }
}
