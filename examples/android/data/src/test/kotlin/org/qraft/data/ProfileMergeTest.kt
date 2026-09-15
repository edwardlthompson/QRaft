package org.qraft.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ProfileMergeTest {
    @Test
    fun newerIncomingWinsOlderDoesNot() {
        val local = QrProfile("a", "Local", "old", updatedAt = 10)
        val current = mapOf("a" to local, "keep" to QrProfile("keep", "Stay", "x", updatedAt = 1))
        val incoming = listOf(
            QrProfile("a", "Remote", "new", updatedAt = 20),
            QrProfile("a", "Stale", "stale", updatedAt = 5),
            QrProfile("b", "New", "y", updatedAt = 1),
        )
        val merged = ProfileMerge.apply(current, incoming)
        assertEquals("new", merged.getValue("a").payloadText)
        assertEquals("Stay", merged.getValue("keep").name)
        assertEquals("New", merged.getValue("b").name)
        assertEquals(setOf("a", "b"), ProfileMerge.wonIds(current, incoming))
    }

    @Test
    fun equalUpdatedAtKeepsLocal() {
        val local = QrProfile("seed-website", "Personal", "https://example.com", updatedAt = 0)
        val incoming = QrProfile("seed-website", "Other", "https://other.example", updatedAt = 0)
        val merged = ProfileMerge.apply(mapOf(local.id to local), listOf(incoming))
        assertEquals("Personal", merged.getValue("seed-website").name)
        assertTrue(ProfileMerge.wonIds(mapOf(local.id to local), listOf(incoming)).isEmpty())
    }

    @Test
    fun sanitizeClearsMissingSidecarPaths() {
        val missing = File("/no/such/qraft-sidecar.png")
        assertFalse(missing.isFile)
        val style = """{"imageBackgroundPath":"${missing.path}","logoImagePath":"/also/missing.png"}"""
        val cleaned = ProfileMerge.sanitize(QrProfile("p", "P", "https://a", styleJson = style))
        assertFalse(cleaned.styleJson.contains("/no/such/qraft-sidecar.png"))
        assertFalse(cleaned.styleJson.contains("/also/missing.png"))
    }

    @Test
    fun sanitizeKeepsExistingFile() {
        val file = File.createTempFile("qraft-bg", ".png")
        file.writeText("x")
        val style = """{"imageBackgroundPath":"${file.absolutePath}"}"""
        val kept = ProfileMerge.sanitize(QrProfile("p", "P", "https://a", styleJson = style))
        assertTrue(kept.styleJson.contains(file.absolutePath))
        file.delete()
    }
}
