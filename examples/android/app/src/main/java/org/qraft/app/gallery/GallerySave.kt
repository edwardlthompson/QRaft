package org.qraft.app.gallery

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import java.io.ByteArrayOutputStream
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.share.QrShare
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRenderer
import org.qraft.widget.WidgetRefresh

object GallerySave {
    suspend fun write(
        context: Context,
        repo: DataStoreProfileRepository,
        name: String,
        draft: EditorDraft,
        style: QrStyle,
        now: Long,
    ): QrProfile? = persist(
        context = context,
        repo = repo,
        id = GalleryStore.newId(),
        name = name,
        draft = draft,
        style = style,
        now = now,
        tags = emptyList(),
        sensitive = false,
        toastRes = R.string.editor_saved_gallery,
    )

    /** Overwrite an existing gallery card (same id) with edited payload/style/name. */
    suspend fun update(
        context: Context,
        repo: DataStoreProfileRepository,
        existing: QrProfile,
        name: String,
        draft: EditorDraft,
        style: QrStyle,
        now: Long,
        tags: List<String> = existing.tags,
    ): QrProfile? = persist(
        context = context,
        repo = repo,
        id = existing.id,
        name = name,
        draft = draft,
        style = style,
        now = now,
        tags = tags,
        sensitive = existing.sensitive,
        toastRes = R.string.profiles_updated,
    )

    private suspend fun persist(
        context: Context,
        repo: DataStoreProfileRepository,
        id: String,
        name: String,
        draft: EditorDraft,
        style: QrStyle,
        now: Long,
        tags: List<String>,
        sensitive: Boolean,
        toastRes: Int,
    ): QrProfile? {
        val sidecar = BgImage.copySidecar(context, id, style.imageBackgroundPath)
        val saved = style.copy(imageBackgroundPath = sidecar.orEmpty())
        val profile = ProfileApply.toProfile(
            id = id,
            name = name,
            draft = draft,
            style = saved,
            sensitive = sensitive,
            now = now,
            tags = tags.ifEmpty { listOf("Website") },
        ) ?: return null
        repo.upsert(profile)
        writeThumb(context, profile, saved)
        WidgetRefresh.afterGalleryEdit(context)
        Toast.makeText(context, toastRes, Toast.LENGTH_SHORT).show()
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
