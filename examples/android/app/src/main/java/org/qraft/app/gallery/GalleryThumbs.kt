package org.qraft.app.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.share.QrShare
import org.qraft.data.QrProfile
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer

object GalleryThumbs {
    fun bitmap(context: Context, profile: QrProfile): Bitmap? {
        GalleryStore.readThumb(context, profile.id)?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.let { return it }
        }
        val style = QrStyleJson.decode(profile.styleJson)
        val matrix = QrShare.encodeOrNull(profile.payloadText, style) ?: return null
        val kind = ProfileApply.fromProfile(profile).first.kind
        val extras = RasterExtrasFactory.of(style, kind, 256)
        return StyledQrRenderer.render(matrix, 256, style, extras, applyCaption = true)
    }
}
