package org.qraft.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

enum class WallpaperTarget { HOME, LOCK, BOTH }

object WallpaperBinder {
    fun displaySizePx(context: Context): Pair<Int, Int> {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= 30) {
            val bounds = wm.currentWindowMetrics.bounds
            return bounds.width() to bounds.height()
        }
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)
        return metrics.widthPixels to metrics.heightPixels
    }

    fun toBitmap(image: WallpaperImage): Bitmap {
        val bmp = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        bmp.setPixels(image.pixels, 0, image.width, 0, 0, image.width, image.height)
        return bmp
    }

    fun flagsFor(target: WallpaperTarget): Int = when (target) {
        WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
        WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
        WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
    }

    fun set(context: Context, image: WallpaperImage, target: WallpaperTarget) {
        val manager = WallpaperManager.getInstance(context)
        val bmp = toBitmap(image)
        manager.setBitmap(bmp, null, true, flagsFor(target))
    }
}
