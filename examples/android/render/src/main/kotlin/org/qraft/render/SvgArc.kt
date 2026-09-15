package org.qraft.render

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

internal object SvgArc {
    fun append(
        x0: Float, y0: Float, rx0: Float, ry0: Float, phiDeg: Float, large: Boolean, sweep: Boolean,
        x1: Float, y1: Float, emit: (Float, Float) -> Unit,
    ) {
        var rx = abs(rx0); var ry = abs(ry0)
        if (rx < 1e-4f || ry < 1e-4f) { emit(x1, y1); return }
        val phi = Math.toRadians(phiDeg.toDouble())
        val cosP = cos(phi); val sinP = sin(phi)
        val dx = (x0 - x1) / 2.0; val dy = (y0 - y1) / 2.0
        val x1p = cosP * dx + sinP * dy
        val y1p = -sinP * dx + cosP * dy
        var rx2 = rx * rx; var ry2 = ry * ry
        val lambda = (x1p * x1p) / rx2 + (y1p * y1p) / ry2
        if (lambda > 1) {
            val s = sqrt(lambda).toFloat(); rx *= s; ry *= s; rx2 = rx * rx; ry2 = ry * ry
        }
        val sign = if (large == sweep) -1.0 else 1.0
        val num = max(0.0, rx2 * ry2 - rx2 * y1p * y1p - ry2 * x1p * x1p)
        val den = rx2 * y1p * y1p + ry2 * x1p * x1p
        val coef = if (den == 0.0) 0.0 else sign * sqrt(num / den)
        val cxp = coef * rx * y1p / ry
        val cyp = coef * -ry * x1p / rx
        val cx = cosP * cxp - sinP * cyp + (x0 + x1) / 2.0
        val cy = sinP * cxp + cosP * cyp + (y0 + y1) / 2.0
        val theta1 = atan2((y1p - cyp) / ry, (x1p - cxp) / rx)
        var dth = atan2((-y1p - cyp) / ry, (-x1p - cxp) / rx) - theta1
        if (!sweep && dth > 0) dth -= 2 * Math.PI
        if (sweep && dth < 0) dth += 2 * Math.PI
        val steps = max(4, (abs(dth) * max(rx, ry) * 2).toInt())
        for (s in 1..steps) {
            val t = theta1 + dth * s / steps
            emit(
                (cx + rx * cos(t) * cosP - ry * sin(t) * sinP).toFloat(),
                (cy + rx * cos(t) * sinP + ry * sin(t) * cosP).toFloat(),
            )
        }
    }
}
