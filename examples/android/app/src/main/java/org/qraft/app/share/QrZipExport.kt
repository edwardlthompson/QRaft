package org.qraft.app.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.qraft.data.QrProfile
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyleJson

object QrZipExport {
    fun zipProfile(context: Context, profile: QrProfile, sidecarBytes: ByteArray?): File {
        val dir = File(context.cacheDir, "qr_export").apply { mkdirs() }
        val file = File(dir, "qraft-${profile.id}.zip")
        ZipOutputStream(file.outputStream()).use { zip ->
            val doc = QrExportDocument(
                payloadText = profile.payloadText,
                style = QrStyleJson.decode(profile.styleJson),
            )
            zip.putNextEntry(ZipEntry("document.json"))
            zip.write(QrExportJson.encode(doc).toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            if (sidecarBytes != null && sidecarBytes.isNotEmpty()) {
                zip.putNextEntry(ZipEntry("background.bin"))
                zip.write(sidecarBytes)
                zip.closeEntry()
            }
        }
        return file
    }

    fun share(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}
