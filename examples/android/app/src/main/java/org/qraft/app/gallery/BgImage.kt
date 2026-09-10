package org.qraft.app.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

object BgImage {
    fun import(context: Context, uri: Uri): String? = runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val decoded = BitmapFactory.decodeStream(input) ?: return null
            val square = Bitmap.createScaledBitmap(decoded, 512, 512, true)
            val dest = File(context.cacheDir, "bg-${System.currentTimeMillis()}.png")
            dest.outputStream().use { square.compress(Bitmap.CompressFormat.PNG, 90, it) }
            if (square !== decoded) square.recycle()
            decoded.recycle()
            dest.absolutePath
        }
    }.getOrNull()

    fun pixels(path: String, sizePx: Int): IntArray? {
        if (path.isBlank() || sizePx <= 0) return null
        val decoded = BitmapFactory.decodeFile(path) ?: return null
        val square = Bitmap.createScaledBitmap(decoded, sizePx, sizePx, true)
        val out = IntArray(sizePx * sizePx)
        square.getPixels(out, 0, sizePx, 0, 0, sizePx, sizePx)
        if (square !== decoded) square.recycle()
        decoded.recycle()
        return out
    }

    fun copySidecar(context: Context, id: String, path: String): String? {
        if (path.isBlank()) return null
        val src = File(path)
        if (!src.isFile) return null
        val dest = GalleryStore.dir(context, id).apply { mkdirs() }.resolve("bg.png")
        src.copyTo(dest, overwrite = true)
        return dest.absolutePath
    }
}
