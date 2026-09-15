package org.qraft.app.editor

/** Whether Home has unsaved payload work that Edit on Home would replace. */
object DraftDirty {
    fun isDirty(draft: EditorDraft, savedPayloads: Collection<String>): Boolean {
        if (draft.primary.isBlank()) return false
        if (isFactoryDefault(draft)) return false
        val encoded = draft.toPayload()?.encodeText()?.trim().orEmpty()
        if (encoded.isNotEmpty() && savedPayloads.any { it == encoded }) return false
        val primary = draft.primary.trim()
        return savedPayloads.none { it == primary }
    }

    fun isFactoryDefault(draft: EditorDraft): Boolean {
        val def = EditorDraft.withKind(draft.kind)
        return draft.kind == def.kind &&
            draft.primary == def.primary &&
            draft.secondary == def.secondary &&
            draft.tertiary == def.tertiary &&
            draft.givenName == def.givenName &&
            draft.familyName == def.familyName &&
            draft.org == def.org &&
            draft.url == def.url
    }
}
