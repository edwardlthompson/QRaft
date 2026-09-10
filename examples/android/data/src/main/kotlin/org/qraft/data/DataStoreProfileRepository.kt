package org.qraft.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.qraftProfiles: DataStore<Preferences> by preferencesDataStore("qraft_profiles")

class DataStoreProfileRepository(
    private val dataStore: DataStore<Preferences>,
) {
    constructor(context: Context) : this(context.qraftProfiles)
    suspend fun upsert(profile: QrProfile) {
        if (profile.id.isBlank()) return
        mutate { current -> current + (profile.id to profile) }
    }

    suspend fun get(id: String): QrProfile? = load()[id]

    suspend fun all(): List<QrProfile> = load().values.toList()

    suspend fun search(query: String): List<QrProfile> = ProfileSearch.filter(all(), query)

    suspend fun delete(id: String) {
        mutate { current -> current - id }
    }

    suspend fun seedIfEmpty() {
        if (all().isNotEmpty()) return
        mutate { current ->
            val next = LinkedHashMap(current)
            seedProfiles().forEach { next[it.id] = it }
            next
        }
    }

    suspend fun exportJson(): String = ProfileCodec.encodeList(all())

    suspend fun importJson(text: String) {
        val incoming = ProfileCodec.decodeList(text)
        if (incoming.isEmpty()) return
        mutate { current ->
            val next = LinkedHashMap(current)
            incoming.forEach { next[it.id] = it }
            next
        }
    }

    private suspend fun load(): LinkedHashMap<String, QrProfile> {
        val raw = dataStore.data.first()[KEY] ?: "[]"
        return ProfileCodec.decodeList(raw).associateByTo(LinkedHashMap()) { it.id }
    }

    private suspend fun mutate(transform: (Map<String, QrProfile>) -> Map<String, QrProfile>) {
        dataStore.edit { prefs ->
            val current = ProfileCodec.decodeList(prefs[KEY] ?: "[]")
                .associateByTo(LinkedHashMap()) { it.id }
            prefs[KEY] = ProfileCodec.encodeList(transform(current).values.toList())
        }
    }

    companion object {
        internal val KEY = stringPreferencesKey("profiles_json")
        const val SEED_ID = "seed-website"
        const val SEED_WORK_ID = "seed-work"
        const val SEED_GUEST_ID = "seed-guest"

        fun seedProfiles(): List<QrProfile> = listOf(
            QrProfile(SEED_ID, "Personal", "https://example.com", tags = listOf("Personal")),
            QrProfile(SEED_WORK_ID, "Work", "mailto:hello@example.com", tags = listOf("Work")),
            QrProfile(SEED_GUEST_ID, "Guest", "WIFI:T:WPA;S:Guest;P:;;", tags = listOf("Guest")),
        )

        fun seedWebsite(): QrProfile = seedProfiles().first()
    }
}
