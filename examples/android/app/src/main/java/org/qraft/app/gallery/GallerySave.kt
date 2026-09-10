package org.qraft.app.gallery

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.share.QrShare
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRenderer

object GallerySave {
    suspend fun write(
        context: Context,
        repo: DataStoreProfileRepository,
        name: String,
        draft: EditorDraft,
        style: QrStyle,
        now: Long,
    ): QrProfile? {
        val id = GalleryStore.newId()
        val sidecar = BgImage.copySidecar(context, id, style.imageBackgroundPath)
        val saved = style.copy(imageBackgroundPath = sidecar.orEmpty())
        val profile = ProfileApply.toProfile(id, name, draft, saved, now = now) ?: return null
        repo.upsert(profile)
        writeThumb(context, profile, saved)
        return profile
    }

    private fun writeThumb(context: Context, profile: QrProfile, style: QrStyle) {
        val matrix = QrShare.encodeOrNull(profile.payloadText, style) ?: return
        val extras = RasterExtrasFactory.of(style, ProfileApply.fromProfile(profile).first.kind, 256)
        val bmp = StyledQrRenderer.render(matrix, 256, style, extras, applyCaption = true)
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 80, bytes)
        bmp.recycle()
        GalleryStore.writeThumb(context, profile.id, bytes.toByteArray())
    }
}
