package org.qraft.app.gallery

import android.content.Context
import java.io.File
import java.util.UUID
import org.qraft.data.QrProfile

object GalleryStore {
    fun newId(): String = UUID.randomUUID().toString()

    fun dir(context: Context, id: String): File =
        File(context.filesDir, "gallery/$id")

    fun delete(context: Context, id: String) {
        dir(context, id).deleteRecursively()
    }

    fun writeThumb(context: Context, id: String, png: ByteArray) {
        dir(context, id).apply { mkdirs() }.resolve("thumb.png").writeBytes(png)
    }

    fun thumbFile(context: Context, id: String): File = dir(context, id).resolve("thumb.png")

    fun readThumb(context: Context, id: String): ByteArray? {
        val file = thumbFile(context, id)
        return if (file.exists()) file.readBytes() else null
    }

    fun documentOf(profile: QrProfile): Pair<String, String> = profile.payloadText to profile.styleJson
}
