package org.qraft.app.gallery

import android.content.Context
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.ProfileCodec
import org.qraft.data.ProfileMerge
import org.qraft.widget.WidgetRefresh

object GalleryRestore {
    suspend fun applyJson(context: Context, repo: DataStoreProfileRepository, json: String): Boolean {
        val incoming = ProfileCodec.decodeList(json)
        if (incoming.isEmpty()) return false
        val current = repo.all().associateBy { it.id }
        ProfileMerge.wonIds(current, incoming).forEach { GalleryStore.delete(context, it) }
        repo.importJson(json)
        WidgetRefresh.afterGalleryEdit(context)
        return true
    }
}
