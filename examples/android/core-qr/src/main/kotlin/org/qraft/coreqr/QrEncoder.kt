package org.qraft.coreqr

import io.nayuki.qrcodegen.DataTooLongException
import io.nayuki.qrcodegen.QrCode

/**
 * Encodes [QrPayload] into a [QrMatrix] via Nayuki QR-Code-generator.
 * All visual styling is applied later by renderers on top of this matrix.
 */
object QrEncoder {
    /**
     * @param forceHighEcc when true (logos / corner badges), use ECC H.
     * @param minVersion optional floor (1–40); Nayuki still picks the smallest fit ≥ floor.
     * @param maxVersion optional ceiling (default 40).
     */
    fun encode(
        payload: QrPayload,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
        forceHighEcc: Boolean = false,
        minVersion: Int = 1,
        maxVersion: Int = 40,
        mask: Int = -1,
        boostEcl: Boolean = true,
    ): QrMatrix {
        require(minVersion in 1..40 && maxVersion in minVersion..40)
        val ecl = if (forceHighEcc) ErrorCorrectionLevel.H else errorCorrection
        val text = payload.encodeText()
        require(text.isNotEmpty()) { "payload encodes to empty text" }
        return encodeText(
            text = text,
            errorCorrection = ecl,
            minVersion = minVersion,
            maxVersion = maxVersion,
            mask = mask,
            boostEcl = boostEcl && !forceHighEcc,
        )
    }

    fun encodeText(
        text: String,
        errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
        minVersion: Int = 1,
        maxVersion: Int = 40,
        mask: Int = -1,
        boostEcl: Boolean = true,
    ): QrMatrix {
        require(text.isNotEmpty()) { "text must not be empty" }
        try {
            val segs = io.nayuki.qrcodegen.QrSegment.makeSegments(text)
            val qr = QrCode.encodeSegments(
                segs,
                errorCorrection.toNayuki(),
                minVersion,
                maxVersion,
                mask,
                boostEcl,
            )
            return fromNayuki(qr)
        } catch (e: DataTooLongException) {
            throw QrEncodeException("Payload does not fit in QR versions $minVersion..$maxVersion", e)
        }
    }

    fun fromNayuki(qr: QrCode): QrMatrix {
        val size = qr.size
        val modules = BooleanArray(size * size) { i ->
            val x = i % size
            val y = i / size
            qr.getModule(x, y)
        }
        return QrMatrix(
            version = qr.version,
            size = size,
            errorCorrection = ErrorCorrectionLevel.fromNayuki(qr.errorCorrectionLevel),
            modules = modules,
        )
    }
}

class QrEncodeException(message: String, cause: Throwable? = null) : Exception(message, cause)
