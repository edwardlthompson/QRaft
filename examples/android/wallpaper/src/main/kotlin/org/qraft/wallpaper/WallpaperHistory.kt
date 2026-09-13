package org.qraft.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.File

/** Snapshot / restore previous system wallpaper before QRaft overwrites it. */
object WallpaperHistory {
    fun snapshot(context: Context, target: WallpaperTarget): Boolean {
        return runCatching {
            val manager = WallpaperManager.getInstance(context)
            when (target) {
                WallpaperTarget.HOME, WallpaperTarget.BOTH ->
                    saveDrawable(manager.drawable, file(context, "home"))
                WallpaperTarget.LOCK -> Unit
            }
            if (target == WallpaperTarget.LOCK || target == WallpaperTarget.BOTH) {
                // Lock drawable API is best-effort; fall back to system drawable.
                val lock = runCatching { manager.getDrawable() }.getOrNull()
                saveDrawable(lock, file(context, "lock"))
            }
            true
        }.getOrDefault(false)
    }

    fun restore(context: Context, target: WallpaperTarget): Boolean {
        return runCatching {
            val manager = WallpaperManager.getInstance(context)
            var ok = false
            if (target == WallpaperTarget.HOME || target == WallpaperTarget.BOTH) {
                decode(file(context, "home"))?.let {
                    manager.setBitmap(it, null, true, WallpaperBinder.flagsFor(WallpaperTarget.HOME))
                    ok = true
                }
            }
            if (target == WallpaperTarget.LOCK || target == WallpaperTarget.BOTH) {
                decode(file(context, "lock"))?.let {
                    manager.setBitmap(it, null, true, WallpaperBinder.flagsFor(WallpaperTarget.LOCK))
                    ok = true
                }
            }
            ok
        }.getOrDefault(false)
    }

    fun hasSnapshot(context: Context, which: String): Boolean = file(context, which).exists()

    private fun file(context: Context, which: String): File =
        File(context.filesDir, "wallpaper-prev-$which.png")

    private fun saveDrawable(drawable: Drawable?, dest: File) {
        val bmp = drawableToBitmap(drawable) ?: return
        dest.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        if (bmp !== (drawable as? BitmapDrawable)?.bitmap) bmp.recycle()
    }

    private fun decode(file: File): Bitmap? =
        if (file.exists()) android.graphics.BitmapFactory.decodeFile(file.absolutePath) else null

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable && drawable.bitmap != null) return drawable.bitmap
        val w = drawable.intrinsicWidth.coerceAtLeast(1)
        val h = drawable.intrinsicHeight.coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bmp
    }
}
