package org.qraft.data

object ProfileSearch {
    enum class Sort { NAME, NEWEST }

    fun filter(all: List<QrProfile>, query: String): List<QrProfile> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all
        return all.filter { profile ->
            profile.name.lowercase().contains(q) ||
                profile.tags.any { it.lowercase().contains(q) } ||
                profile.payloadText.lowercase().contains(q)
        }
    }

    fun sort(all: List<QrProfile>, sort: Sort): List<QrProfile> = when (sort) {
        Sort.NAME -> all.sortedBy { it.name.lowercase() }
        Sort.NEWEST -> all.sortedByDescending { it.updatedAt }
    }
}
