package org.qraft.render

import android.graphics.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import org.qraft.coreqr.QrMatrix

object QrPdfExporter {
    fun export(matrix: QrMatrix, style: QrStyle = QrStyle.DEFAULT, pagePt: Int = 612): ByteArray {
        require(pagePt > 72)
        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(pagePt, pagePt, 1).create())
        val bmp = StyledQrRenderer.render(matrix, pagePt - 72, style)
        page.canvas.drawBitmap(bmp, 36f, 36f, null)
        pdf.finishPage(page)
        val out = ByteArrayOutputStream()
        pdf.writeTo(out)
        pdf.close()
        bmp.recycle()
        return out.toByteArray()
    }
}

object StyleJsonQr {
    fun payload(style: QrStyle): String = QrStyleJson.encode(style)
}
