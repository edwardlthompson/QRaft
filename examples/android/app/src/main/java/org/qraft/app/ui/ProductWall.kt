package org.qraft.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.qraft.app.editor.EditorDraft
import org.qraft.app.share.QrShare
import org.qraft.render.QrStyle
import org.qraft.wallpaper.WallpaperBinder
import org.qraft.wallpaper.WallpaperComposer
import org.qraft.wallpaper.WallpaperTarget

internal fun setWallpaperFromDraft(
    context: Context,
    draft: EditorDraft,
    style: QrStyle,
    size: Pair<Int, Int>,
    margin: Float,
    target: WallpaperTarget,
) {
    val text = draft.toPayload()?.encodeText() ?: return
    val matrix = QrShare.encodeOrNull(text, style, org.qraft.coreqr.QrSurface.WALLPAPER) ?: return
    val image = WallpaperComposer.compose(matrix, size.first, size.second, style, margin.toDouble())
    WallpaperBinder.set(context, image, target)
}

internal fun writeWallpaperPng(
    context: Context,
    uri: Uri,
    draft: EditorDraft,
    style: QrStyle,
    size: Pair<Int, Int>,
    margin: Float,
) {
    val text = draft.toPayload()?.encodeText() ?: return
    val matrix = QrShare.encodeOrNull(text, style, org.qraft.coreqr.QrSurface.WALLPAPER) ?: return
    val image = WallpaperComposer.compose(matrix, size.first, size.second, style, margin.toDouble())
    val bmp = WallpaperBinder.toBitmap(image)
    context.contentResolver.openOutputStream(uri)?.use { out ->
        bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    bmp.recycle()
}
