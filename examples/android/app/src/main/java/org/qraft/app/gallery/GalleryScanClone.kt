package org.qraft.app.gallery

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.qraft.app.R
import org.qraft.app.editor.PayloadKind
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.editor.VCardDraft
import org.qraft.app.share.QrShare
import org.qraft.app.share.ShareIntake
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer
import org.qraft.scan.WifiScanParse
import org.qraft.widget.WidgetRefresh
import org.qraft.app.editor.ProfileApply

/** Save a scanned QR payload into the gallery as an exact clone. */
object GalleryScanClone {
    suspend fun save(
        context: Context,
        repo: DataStoreProfileRepository,
        payload: String,
        now: Long = System.currentTimeMillis(),
    ): QrProfile? = withContext(Dispatchers.IO) {
        val text = payload.trim()
        if (text.isEmpty()) return@withContext null
        val id = GalleryStore.newId()
        val style = QrStyle.DEFAULT
        val profile = QrProfile(
            id = id,
            name = suggestedName(text),
            payloadText = text,
            tags = listOf("Scanned"),
            styleJson = QrStyleJson.encode(style),
            sensitive = false,
            updatedAt = now,
        )
        repo.upsert(profile)
        writeThumb(context, profile, style)
        WidgetRefresh.afterGalleryEdit(context)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, R.string.scan_saved_gallery, Toast.LENGTH_SHORT).show()
        }
        profile
    }

    fun suggestedName(payload: String): String {
        val t = payload.trim()
        when {
            t.startsWith("BEGIN:VCARD", ignoreCase = true) -> {
                val draft = VCardDraft.fromPayloadText(t)
                val name = listOf(draft.givenName, draft.familyName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                return name.ifBlank { "Contact" }
            }
            t.startsWith("WIFI:", ignoreCase = true) ->
                return WifiScanParse.parse(t)?.ssid?.ifBlank { "Wi-Fi" } ?: "Wi-Fi"
            t.startsWith("http://", true) || t.startsWith("https://", true) -> {
                val host = runCatching { java.net.URI(t).host }.getOrNull().orEmpty()
                return host.ifBlank { "Link" }
            }
            else -> {
                val draft = ShareIntake.parse(t)
                if (draft != null && draft.kind != PayloadKind.Text) {
                    return draft.primary.take(40).ifBlank { "Scanned QR" }
                }
                return t.lineSequence().firstOrNull()?.take(40)?.ifBlank { "Scanned QR" }
                    ?: "Scanned QR"
            }
        }
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
