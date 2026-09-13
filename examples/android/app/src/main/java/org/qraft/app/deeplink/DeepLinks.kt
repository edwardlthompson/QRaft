package org.qraft.app.deeplink

import android.content.Intent
import android.net.Uri

/** Parses `qraft://profile/{id}` (and optional `EXTRA_PROFILE_ID`) for gallery open. */
object DeepLinks {
    const val SCHEME = "qraft"
    const val HOST_PROFILE = "profile"
    const val EXTRA_PROFILE_ID = "org.qraft.app.PROFILE_ID"

    fun profileId(intent: Intent?): String? {
        if (intent == null) return null
        intent.getStringExtra(EXTRA_PROFILE_ID)?.takeIf { it.isNotBlank() }?.let { return it }
        return profileIdFromUri(intent.data)
    }

    fun profileIdFromUri(uri: Uri?): String? {
        if (uri == null) return null
        if (!SCHEME.equals(uri.scheme, ignoreCase = true)) return null
        if (!HOST_PROFILE.equals(uri.host, ignoreCase = true)) return null
        return uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
    }

    fun profileUri(id: String): Uri =
        Uri.Builder().scheme(SCHEME).authority(HOST_PROFILE).appendPath(id).build()
}
