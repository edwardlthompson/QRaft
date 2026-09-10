package org.qraft.data

data class ProfileSnapshot(
    val payloadText: String,
    val styleJson: String,
    val at: Long = 0L,
)

object ProfileHistory {
    const val MAX = 8

    fun push(current: List<ProfileSnapshot>, next: ProfileSnapshot): List<ProfileSnapshot> =
        (current + next).takeLast(MAX)

    fun undo(current: List<ProfileSnapshot>): Pair<ProfileSnapshot?, List<ProfileSnapshot>> {
        if (current.isEmpty()) return null to current
        return current.last() to current.dropLast(1)
    }
}
