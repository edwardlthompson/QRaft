package org.qraft.data

/**
 * Saved QR profile (DataStore/Room wiring comes next). Style is opaque JSON for now
 * so render packs can evolve without breaking persistence.
 */
data class QrProfile(
    val id: String,
    val name: String,
    val payloadText: String,
    val tags: List<String> = emptyList(),
    val styleJson: String = "{}",
    val sensitive: Boolean = false,
)

/**
 * In-memory profile store scaffold. Replace with DataStore + optional Room.
 */
class ProfileRepository {
    private val profiles = linkedMapOf<String, QrProfile>()

    fun upsert(profile: QrProfile) {
        profiles[profile.id] = profile
    }

    fun get(id: String): QrProfile? = profiles[id]

    fun all(): List<QrProfile> = profiles.values.toList()

    fun search(query: String): List<QrProfile> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all()
        return all().filter { profile ->
            profile.name.lowercase().contains(q) ||
                profile.tags.any { it.lowercase().contains(q) } ||
                profile.payloadText.lowercase().contains(q)
        }
    }

    fun delete(id: String) {
        profiles.remove(id)
    }
}
