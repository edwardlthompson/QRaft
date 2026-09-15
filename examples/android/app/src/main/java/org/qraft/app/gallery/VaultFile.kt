package org.qraft.app.gallery

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object VaultFile {
    const val MAX_BYTES = 2 * 1024 * 1024

    fun write(resolver: ContentResolver, uri: Uri, blob: String): Boolean = runCatching {
        resolver.openOutputStream(uri)?.use { stream ->
            stream.write(blob.toByteArray(Charsets.UTF_8))
        } ?: return false
        true
    }.getOrDefault(false)

    fun read(resolver: ContentResolver, uri: Uri, maxBytes: Int = MAX_BYTES): String? = runCatching {
        resolver.openInputStream(uri)?.use { stream ->
            val bytes = readBounded(stream, maxBytes) ?: return@runCatching null
            String(bytes, Charsets.UTF_8)
        }
    }.getOrNull()

    fun readBounded(stream: InputStream, maxBytes: Int = MAX_BYTES): ByteArray? {
        val buf = ByteArrayOutputStream()
        val tmp = ByteArray(8192)
        var total = 0
        while (true) {
            val n = stream.read(tmp)
            if (n < 0) break
            total += n
            if (total > maxBytes) return null
            buf.write(tmp, 0, n)
        }
        if (total == 0) return null
        return buf.toByteArray()
    }
}
