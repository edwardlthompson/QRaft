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
    val updatedAt: Long = 0L,
)

/**
 * In-memory profile store. Persistent path is [DataStoreProfileRepository].
 */
class ProfileRepository {
    private val profiles = linkedMapOf<String, QrProfile>()

    fun upsert(profile: QrProfile) {
        profiles[profile.id] = profile
    }

    fun get(id: String): QrProfile? = profiles[id]

    fun all(): List<QrProfile> = profiles.values.toList()

    fun search(query: String): List<QrProfile> = ProfileSearch.filter(all(), query)

    fun delete(id: String) {
        profiles.remove(id)
    }
}
