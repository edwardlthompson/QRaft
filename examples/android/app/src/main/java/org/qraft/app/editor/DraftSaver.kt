package org.qraft.app.editor

import androidx.compose.runtime.saveable.listSaver

val DraftSaver = listSaver<EditorDraft, String>(
    save = {
        listOf(
            it.kind.name, it.primary, it.secondary, it.tertiary, it.wifiSecurity.name,
            if (it.wifiHidden) "1" else "0",
            it.org, it.url,
        )
    },
    restore = {
        EditorDraft.fromParts(
            it[0], it[1], it[2], it[3], it[4],
            wifiHidden = it.getOrNull(5) == "1",
            org = it.getOrNull(6) ?: "",
            url = it.getOrNull(7) ?: "",
        )
    },
)
