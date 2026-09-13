package org.qraft.app.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import org.qraft.coreqr.EccPolicy
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrMatrix
import org.qraft.coreqr.QrSurface
import org.qraft.render.QrPdfExporter
import org.qraft.render.QrStyle
import org.qraft.render.QrSvgExporter
import org.qraft.render.RasterExtras
import org.qraft.render.StyledQrRenderer

object QrShare {
    fun pngFile(
        context: Context,
        matrix: QrMatrix,
        style: QrStyle,
        extras: RasterExtras = RasterExtras(),
        sizePx: Int = ExportPngSize.DEFAULT.px,
    ): File {
        val dir = File(context.cacheDir, "qr_export").apply { mkdirs() }
        val file = File(dir, "qraft.png")
        val edge = sizePx.coerceIn(256, 4096)
        val bmp: Bitmap = StyledQrRenderer.render(matrix, edge, style, extras, applyCaption = true)
        file.outputStream().use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out) }
        bmp.recycle()
        return file
    }

    fun sharePng(
        context: Context,
        matrix: QrMatrix,
        style: QrStyle,
        extras: RasterExtras = RasterExtras(),
        sizePx: Int = ExportPngSize.DEFAULT.px,
    ) {
        val file = pngFile(context, matrix, style, extras, sizePx)
        shareFile(context, file, "image/png")
    }

    fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String = "qraft-poster.png") {
        val dir = File(context.cacheDir, "qr_export").apply { mkdirs() }
        val file = File(dir, fileName)
        file.outputStream().use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        shareFile(context, file, "image/png")
    }

    private fun shareFile(context: Context, file: File, mime: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun sharePdf(context: Context, matrix: QrMatrix, style: QrStyle) {
        val dir = File(context.cacheDir, "qr_export").apply { mkdirs() }
        val file = File(dir, "qraft.pdf")
        file.writeBytes(pdfBytes(matrix, style))
        shareFile(context, file, "application/pdf")
    }

    fun svgText(matrix: QrMatrix, style: QrStyle): String = QrSvgExporter.export(matrix, style)

    fun pdfBytes(matrix: QrMatrix, style: QrStyle): ByteArray = QrPdfExporter.export(matrix, style)

    fun encodeOrNull(text: String, style: QrStyle, surface: QrSurface = QrSurface.EXPORT): QrMatrix? {
        val overlay = style.hasOverlay
        return runCatching {
            QrEncoder.encodeText(
                text,
                errorCorrection = EccPolicy.choose(surface, overlay),
                boostEcl = EccPolicy.boostEcl(surface, overlay),
            )
        }.getOrNull()
    }
}
