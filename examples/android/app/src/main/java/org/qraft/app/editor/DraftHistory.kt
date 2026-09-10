package org.qraft.app.editor

data class DraftSnap(
    val draft: EditorDraft,
    val styleJson: String,
    val name: String,
)

object DraftHistory {
    const val MAX = 16

    fun push(past: List<DraftSnap>, future: List<DraftSnap>, next: DraftSnap): Pair<List<DraftSnap>, List<DraftSnap>> {
        if (past.lastOrNull() == next) return past to future
        return (past + next).takeLast(MAX) to emptyList()
    }

    fun undo(past: List<DraftSnap>, future: List<DraftSnap>): Triple<DraftSnap?, List<DraftSnap>, List<DraftSnap>> {
        if (past.size < 2) return Triple(past.lastOrNull(), past, future)
        val current = past.last()
        val prev = past[past.lastIndex - 1]
        return Triple(prev, past.dropLast(1), listOf(current) + future)
    }

    fun redo(past: List<DraftSnap>, future: List<DraftSnap>): Triple<DraftSnap?, List<DraftSnap>, List<DraftSnap>> {
        val next = future.firstOrNull() ?: return Triple(past.lastOrNull(), past, future)
        return Triple(next, past + next, future.drop(1))
    }
}
