package org.qraft.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class ProfileSearchTest {
    @Test
    fun emptyQueryReturnsAll() {
        val all = listOf(QrProfile("1", "Work Wi-Fi", "WIFI:T:WPA;S:x;P:y;"))
        assertEquals(all, ProfileSearch.filter(all, "  "))
    }

    @Test
    fun matchesNameTagAndPayload() {
        val all = listOf(
            QrProfile("1", "Work", "https://a", tags = listOf("office")),
            QrProfile("2", "Home", "WIFI:T:WPA;S:lab;P:z;"),
        )
        assertEquals(listOf("1"), ProfileSearch.filter(all, "OFFICE").map { it.id })
        assertEquals(listOf("2"), ProfileSearch.filter(all, "lab").map { it.id })
        assertEquals(listOf("1"), ProfileSearch.filter(all, "work").map { it.id })
    }

    @Test
    fun sortByNameAndNewest() {
        val all = listOf(
            QrProfile("1", "Zed", "a", updatedAt = 1),
            QrProfile("2", "Ada", "b", updatedAt = 9),
        )
        assertEquals(listOf("2", "1"), ProfileSearch.sort(all, ProfileSearch.Sort.NAME).map { it.id })
        assertEquals(listOf("2", "1"), ProfileSearch.sort(all, ProfileSearch.Sort.NEWEST).map { it.id })
    }
}

class ProfileTagsTest {
    @Test
    fun parseSplitsAndDedupes() {
        assertEquals(listOf("Work", "wifi"), ProfileTags.parse(" Work, wifi,Work "))
        assertEquals("", ProfileTags.format(emptyList()))
        assertEquals("a, b", ProfileTags.format(listOf("a", "b")))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ProfileCodecTest {
    @Test
    fun blankAndInvalidDecodeEmpty() {
        assertTrue(ProfileCodec.decodeList("").isEmpty())
        assertTrue(ProfileCodec.decodeList("not-json").isEmpty())
        assertTrue(ProfileCodec.decodeList("[{}]").isEmpty())
    }

    @Test
    fun roundTripKeepsFields() {
        val src = listOf(
            QrProfile("p1", "Guest Wi-Fi", "WIFI:T:nopass;S:g;", tags = listOf("guest"), styleJson = "{\"x\":1}"),
        )
        assertEquals(src, ProfileCodec.decodeList(ProfileCodec.encodeList(src)))
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class DataStoreProfileRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun resetStore() {
        context.filesDir.resolve("datastore").listFiles()?.forEach { file: File -> file.delete() }
    }

    @Test
    fun persistsSearchAndDelete() = runBlocking {
        val repo = DataStoreProfileRepository(context)
        repo.upsert(QrProfile("a", "Work site", "https://work.example", tags = listOf("work")))
        repo.upsert(QrProfile("", "ignored", "x"))
        assertEquals(1, repo.all().size)
        assertEquals("a", repo.search("WORK").single().id)
        repo.delete("a")
        assertTrue(repo.all().isEmpty())
        repo.seedIfEmpty()
        assertEquals(
            listOf("Personal", "Work", "Guest"),
            repo.all().map { it.name },
        )
        repo.seedIfEmpty()
        assertEquals(3, repo.all().size)
        repo.importJson(repo.exportJson())
        assertEquals(3, repo.all().size)
    }

    @Test
    fun importMergesByUpdatedAtAndKeepsLocalOnly() = runBlocking {
        val repo = DataStoreProfileRepository(context)
        repo.upsert(QrProfile("a", "Local", "old", updatedAt = 10))
        repo.upsert(QrProfile("keep", "Stay", "x", updatedAt = 1))
        repo.importJson(
            ProfileCodec.encodeList(
                listOf(
                    QrProfile("a", "Remote", "new", updatedAt = 20),
                    QrProfile("a", "Stale", "stale", updatedAt = 5),
                    QrProfile("b", "New", "y", updatedAt = 1),
                ),
            ),
        )
        val all = repo.all().associateBy { it.id }
        assertEquals("new", all.getValue("a").payloadText)
        assertEquals("Stay", all.getValue("keep").name)
        assertEquals("New", all.getValue("b").name)
    }

    @Test
    fun importClearsMissingBackgroundPath() = runBlocking {
        val repo = DataStoreProfileRepository(context)
        val missing = "/no/such/qraft-import-bg.png"
        repo.importJson(
            ProfileCodec.encodeList(
                listOf(QrProfile("p", "P", "https://a", styleJson = """{"imageBackgroundPath":"$missing"}""")),
            ),
        )
        assertFalse(repo.get("p")!!.styleJson.contains(missing))
    }
}
