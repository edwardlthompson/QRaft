package org.qraft.coreqr

/**
 * Immutable boolean QR module grid. Indexing is (x, y) with origin top-left;
 * [isDark] true means a dark module. Size is modules per side (21…177).
 */
data class QrMatrix(
    val version: Int,
    val size: Int,
    val errorCorrection: ErrorCorrectionLevel,
    private val modules: BooleanArray,
) {
    init {
        require(version in 1..40) { "version must be 1..40, was $version" }
        require(size == version * 4 + 17) { "size $size does not match version $version" }
        require(modules.size == size * size) { "module buffer length mismatch" }
    }

    fun isDark(x: Int, y: Int): Boolean {
        if (x !in 0 until size || y !in 0 until size) return false
        return modules[y * size + x]
    }

    /** Row-major copy of dark modules (true = dark). */
    fun toBooleanArray(): BooleanArray = modules.copyOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QrMatrix) return false
        return version == other.version &&
            size == other.size &&
            errorCorrection == other.errorCorrection &&
            modules.contentEquals(other.modules)
    }

    override fun hashCode(): Int {
        var result = version
        result = 31 * result + size
        result = 31 * result + errorCorrection.hashCode()
        result = 31 * result + modules.contentHashCode()
        return result
    }
}
