package org.qraft.app.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.IntentCompat
import java.io.InputStream
import org.qraft.app.editor.EditorDraft

object ShareIntents {
    fun applyIncoming(context: Context, intent: Intent?, setDraft: (EditorDraft) -> Unit) {
        if (intent == null) return
        val extra = intent.getStringExtra(Intent.EXTRA_TEXT)
            ?: intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
            ?: intent.dataString
        val stream = readFromUri(
            context.contentResolver,
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java),
        )
        fromIntent(intent.action, intent.type, extra, stream)?.let(setDraft)
        if (extra == null && stream == null) ShareIntake.parse(intent.dataString)?.let(setDraft)
    }

    fun fromIntent(action: String?, type: String?, extraText: String?, streamText: String?): EditorDraft? {
        val mime = type.orEmpty()
        when (action) {
            Intent.ACTION_SEND, Intent.ACTION_SENDTO, Intent.ACTION_VIEW, Intent.ACTION_PROCESS_TEXT -> {
                ShareIntake.parse(extraText, mime)?.let { return it }
                ShareIntake.parse(streamText, mime)?.let { return it }
            }
        }
        return null
    }

    fun readUtf8(stream: InputStream?): String? {
        if (stream == null) return null
        return runCatching { stream.bufferedReader().use { it.readText() } }.getOrNull()
    }

    fun readFromUri(resolver: android.content.ContentResolver, uri: Uri?): String? {
        if (uri == null) return null
        return runCatching { resolver.openInputStream(uri)?.use { readUtf8(it) } }.getOrNull()
    }
}
