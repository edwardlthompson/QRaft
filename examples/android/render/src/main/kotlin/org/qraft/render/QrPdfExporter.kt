package org.qraft.render

import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import org.qraft.coreqr.QrMatrix

object QrPdfExporter {
    fun export(matrix: QrMatrix, style: QrStyle = QrStyle.DEFAULT, pagePt: Int = 612): ByteArray {
        return exportPages(listOf(matrix to style), pagePt)
    }

    fun exportPages(pages: List<Pair<QrMatrix, QrStyle>>, pagePt: Int = 612): ByteArray {
        require(pagePt > 72)
        if (pages.isEmpty()) return ByteArray(0)
        val pdf = PdfDocument()
        val bitmaps = mutableListOf<android.graphics.Bitmap>()
        pages.forEachIndexed { index, (matrix, style) ->
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(pagePt, pagePt, index + 1).create())
            val bmp = StyledQrRenderer.render(matrix, pagePt - 72, style)
            bitmaps.add(bmp)
            page.canvas.drawBitmap(bmp, 36f, 36f, null)
            pdf.finishPage(page)
        }
        val out = ByteArrayOutputStream()
        pdf.writeTo(out)
        pdf.close()
        bitmaps.forEach { it.recycle() }
        return out.toByteArray()
    }
}

object StyleJsonQr {
    fun payload(style: QrStyle): String = QrStyleJson.encode(style)
}
