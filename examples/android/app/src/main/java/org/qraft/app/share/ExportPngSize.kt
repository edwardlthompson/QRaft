package org.qraft.app.share

/** Export PNG edge length presets (square). */
enum class ExportPngSize(val px: Int) {
    S512(512),
    S1024(1024),
    S2048(2048),
    ;

    companion object {
        val DEFAULT = S1024
    }
}
