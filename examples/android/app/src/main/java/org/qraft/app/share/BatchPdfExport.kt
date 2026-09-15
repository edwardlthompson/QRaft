package org.qraft.app.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import org.qraft.data.QrProfile
import org.qraft.render.QrPdfExporter
import org.qraft.render.QrStyleJson

object BatchPdfExport {
    fun pdfBytes(profiles: List<QrProfile>): ByteArray {
        if (profiles.isEmpty()) return ByteArray(0)
        val built = profiles.mapNotNull { profile ->
            val style = QrStyleJson.decode(profile.styleJson)
            val matrix = QrShare.encodeOrNull(profile.payloadText, style) ?: return@mapNotNull null
            Triple(matrix, style, profile.payloadText)
        }
        return QrPdfExporter.exportPages(
            built.map { it.first to it.second },
            payloads = built.map { it.third },
        )
    }

    fun share(context: Context, profiles: List<QrProfile>) {
        val bytes = pdfBytes(profiles)
        if (bytes.isEmpty()) return
        val dir = File(context.cacheDir, "qr_export").apply { mkdirs() }
        val file = File(dir, "qraft-gallery.pdf")
        file.writeBytes(bytes)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
