package org.qraft.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.coreqr.QrEncoder

class BlobModulesTest {
    @Test
    fun blobBridgesAdjacentDarkModules() {
        val matrix = QrEncoder.encodeText("blob-join")
        val blob = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(moduleShape = ModuleShape.BLOB))
        val dot = StyledQrRasterizer.rasterize(matrix, 256, QrStyle(moduleShape = ModuleShape.DOT))
        val quiet = QrStyle.DEFAULT.quietZoneModules
        var found = false
        for (y in 0 until matrix.size) {
            for (x in 0 until matrix.size - 1) {
                if (!BlobModules.darkData(matrix, x, y) || !BlobModules.darkData(matrix, x + 1, y)) continue
                val px = blob.contentOriginX + (x + quiet) * blob.modulePx + blob.modulePx
                val py = blob.contentOriginY + (y + quiet) * blob.modulePx + blob.modulePx / 2
                if (px !in 0 until blob.width || py !in 0 until blob.height) continue
                assertEquals(0xFF000000.toInt(), blob.pixels[py * blob.width + px])
                assertEquals(0xFFFFFFFF.toInt(), dot.pixels[py * dot.width + px])
                found = true
                return
            }
        }
        assertTrue(found)
    }
}
