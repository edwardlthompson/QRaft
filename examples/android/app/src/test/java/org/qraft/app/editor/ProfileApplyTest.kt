package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.qraft.data.QrProfile
import org.qraft.render.ModuleShape
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ProfileApplyTest {
    @Test
    fun urlRoundTrip() {
        val style = QrStyle(moduleShape = ModuleShape.DIAMOND)
        val profile = ProfileApply.toProfile("id", "Site", EditorDraft(primary = "https://example.com"), style, now = 9)!!
        assertEquals("https://example.com", profile.payloadText)
        assertEquals(9L, profile.updatedAt)
        val (draft, loaded) = ProfileApply.fromProfile(profile)
        assertEquals(PayloadKind.Url, draft.kind)
        assertEquals(ModuleShape.DIAMOND, loaded.moduleShape)
        assertEquals(listOf("Website"), profile.tags)
    }

    @Test
    fun customTagsSurviveToProfile() {
        val profile = ProfileApply.toProfile(
            "id", "Site", EditorDraft(primary = "https://example.com"), QrStyle.DEFAULT, now = 1, tags = listOf("home"),
        )!!
        assertEquals(listOf("home"), profile.tags)
    }

    @Test
    fun emptyDraftIsNull() {
        assertNull(ProfileApply.toProfile("id", "x", EditorDraft(kind = PayloadKind.Text, primary = ""), QrStyle.DEFAULT, now = 1))
    }

    @Test
    fun styleJsonRoundTripOnProfile() {
        val src = QrProfile("p", "n", "hello", styleJson = QrStyleJson.encode(QrStyle(moduleShape = ModuleShape.BLOB)))
        assertEquals(ModuleShape.BLOB, ProfileApply.fromProfile(src).second.moduleShape)
    }
}
