package org.qraft.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.qraft.app.editor.EditorDraft
import org.qraft.app.share.QrShare
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.wallpaper.WallpaperBinder
import org.qraft.wallpaper.WallpaperComposer
import org.qraft.wallpaper.WallpaperHistory
import org.qraft.wallpaper.WallpaperImage
import org.qraft.wallpaper.WallpaperPair
import org.qraft.wallpaper.WallpaperTarget

internal fun setWallpaperFromDraft(
    context: Context,
    draft: EditorDraft,
    style: QrStyle,
    size: Pair<Int, Int>,
    margin: Float,
    target: WallpaperTarget,
    pairDarkLight: Boolean = false,
) {
    val text = draft.toPayload()?.encodeText() ?: return
    val matrix = QrShare.encodeOrNull(text, style, org.qraft.coreqr.QrSurface.WALLPAPER) ?: return
    WallpaperHistory.snapshot(context, target)
    val home = WallpaperComposer.compose(matrix, size.first, size.second, style, margin.toDouble())
    when {
        pairDarkLight && target == WallpaperTarget.BOTH -> {
            WallpaperBinder.set(context, home, WallpaperTarget.HOME)
            WallpaperBinder.set(
                context,
                lockImage(matrix, size, WallpaperPair.darkVariant(style), margin),
                WallpaperTarget.LOCK,
            )
        }
        target == WallpaperTarget.BOTH -> {
            WallpaperBinder.set(context, home, WallpaperTarget.HOME)
            WallpaperBinder.set(context, lockImage(matrix, size, style, margin), WallpaperTarget.LOCK)
        }
        target == WallpaperTarget.LOCK ->
            WallpaperBinder.set(context, lockImage(matrix, size, style, margin), WallpaperTarget.LOCK)
        else -> WallpaperBinder.set(context, home, WallpaperTarget.HOME)
    }
}

internal fun setWallpaperFromProfile(
    context: Context,
    profile: QrProfile,
    size: Pair<Int, Int>,
    margin: Float,
    target: WallpaperTarget,
) {
    val style = QrStyleJson.decode(profile.styleJson)
    val matrix = QrShare.encodeOrNull(
        profile.payloadText,
        style,
        org.qraft.coreqr.QrSurface.WALLPAPER,
    ) ?: return
    WallpaperHistory.snapshot(context, target)
    val image = when (target) {
        WallpaperTarget.LOCK -> lockImage(matrix, size, style, margin)
        WallpaperTarget.BOTH -> {
            WallpaperBinder.set(
                context,
                WallpaperComposer.compose(matrix, size.first, size.second, style, margin.toDouble()),
                WallpaperTarget.HOME,
            )
            lockImage(matrix, size, style, margin)
        }
        WallpaperTarget.HOME ->
            WallpaperComposer.compose(matrix, size.first, size.second, style, margin.toDouble())
    }
    WallpaperBinder.set(context, image, if (target == WallpaperTarget.BOTH) WallpaperTarget.LOCK else target)
}

internal fun restorePreviousWallpaper(context: Context, target: WallpaperTarget): Boolean =
    WallpaperHistory.restore(context, target)

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

private fun lockImage(
    matrix: org.qraft.coreqr.QrMatrix,
    size: Pair<Int, Int>,
    style: QrStyle,
    margin: Float,
): WallpaperImage = WallpaperComposer.composeLock(
    matrix,
    size.first,
    size.second,
    style,
    margin.toDouble(),
)
