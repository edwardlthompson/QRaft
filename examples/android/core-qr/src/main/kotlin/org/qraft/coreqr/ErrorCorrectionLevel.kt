package org.qraft.coreqr

/**
 * QR Model 2 error correction levels (ISO/IEC 18004).
 */
enum class ErrorCorrectionLevel {
    L,
    M,
    Q,
    H,
    ;

    fun toNayuki(): io.nayuki.qrcodegen.QrCode.Ecc = when (this) {
        L -> io.nayuki.qrcodegen.QrCode.Ecc.LOW
        M -> io.nayuki.qrcodegen.QrCode.Ecc.MEDIUM
        Q -> io.nayuki.qrcodegen.QrCode.Ecc.QUARTILE
        H -> io.nayuki.qrcodegen.QrCode.Ecc.HIGH
    }

    companion object {
        fun fromNayuki(ecc: io.nayuki.qrcodegen.QrCode.Ecc): ErrorCorrectionLevel = when (ecc) {
            io.nayuki.qrcodegen.QrCode.Ecc.LOW -> L
            io.nayuki.qrcodegen.QrCode.Ecc.MEDIUM -> M
            io.nayuki.qrcodegen.QrCode.Ecc.QUARTILE -> Q
            io.nayuki.qrcodegen.QrCode.Ecc.HIGH -> H
        }
    }
}
