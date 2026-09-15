package org.qraft.app.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.render.CaptionSpec
import org.qraft.render.ColorTheme
import org.qraft.render.FinderShape
import org.qraft.render.GradientSpec
import org.qraft.render.ModuleShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle

class EditorStepsTest {
    @Test
    fun nextWalksThenStopsOnPlace() {
        assertEquals(EditorStep.Look, EditorSteps.next(EditorStep.Content))
        assertEquals(EditorStep.Place, EditorSteps.next(EditorStep.Look))
        assertEquals(EditorStep.Place, EditorSteps.next(EditorStep.Place))
        assertEquals(EditorStep.Place, EditorSteps.next(EditorStep.Share))
    }

    @Test
    fun previousWalksThenStopsOnContent() {
        assertEquals(EditorStep.Content, EditorSteps.previous(EditorStep.Content))
        assertEquals(EditorStep.Content, EditorSteps.previous(EditorStep.Look))
        assertEquals(EditorStep.Look, EditorSteps.previous(EditorStep.Place))
        assertEquals(EditorStep.Look, EditorSteps.previous(EditorStep.Share))
    }

    @Test
    fun consumeBackLeavesContent() {
        assertNull(EditorSteps.consumeBack(EditorStep.Content))
        assertEquals(EditorStep.Content, EditorSteps.consumeBack(EditorStep.Look))
        assertEquals(EditorStep.Look, EditorSteps.consumeBack(EditorStep.Place))
        assertEquals(EditorStep.Look, EditorSteps.consumeBack(EditorStep.Share))
    }

    @Test
    fun chipsAreContentLookSave() {
        assertEquals(
            listOf(EditorStep.Content, EditorStep.Look, EditorStep.Place),
            EditorSteps.CHIPS,
        )
        assertEquals(EditorStep.Place, EditorSteps.shown(EditorStep.Share))
        assertFalse(EditorSteps.SHOW_CHIPS)
    }

    @Test
    fun moreTypesOpensForNonFrequentKinds() {
        assertEquals(
            listOf(PayloadKind.Url, PayloadKind.Wifi, PayloadKind.Text, PayloadKind.VCard),
            EditorSteps.FREQUENT,
        )
        assertFalse(EditorSteps.showMoreTypes(PayloadKind.Url))
        assertFalse(EditorSteps.showMoreTypes(PayloadKind.Wifi))
        assertFalse(EditorSteps.showMoreTypes(PayloadKind.Text))
        assertFalse(EditorSteps.showMoreTypes(PayloadKind.VCard))
        assertTrue(EditorSteps.showMoreTypes(PayloadKind.Email))
        assertTrue(EditorSteps.showMoreTypes(PayloadKind.Sms))
    }

    @Test
    fun advancedOpensForNonDefaultLook() {
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT))
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(moduleShape = ModuleShape.ROUNDED)))
        assertTrue(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(finderShape = FinderShape.ROUNDED)))
        assertTrue(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(finderPupil = FinderShape.CIRCLE)))
        assertTrue(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(frame = QrFrame.THIN)))
        assertTrue(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(cornerBadge = true)))
        assertTrue(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(gradient = GradientSpec(enabled = true))))
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(colorTheme = ColorTheme.OLED)))
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(imageBackgroundPath = "/tmp/bg.png")))
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(logoImagePath = "/tmp/logo.png")))
        assertFalse(EditorSteps.isAdvanced(QrStyle.DEFAULT.copy(caption = CaptionSpec("hi"))))
    }

    @Test
    fun showEccOnlyOnLookWithOverlay() {
        assertFalse(EditorSteps.showEcc(EditorStep.Content, overlay = true))
        assertFalse(EditorSteps.showEcc(EditorStep.Look, overlay = false))
        assertTrue(EditorSteps.showEcc(EditorStep.Look, overlay = true))
        assertFalse(EditorSteps.showEcc(EditorStep.Place, overlay = true))
        assertFalse(EditorSteps.showEcc(EditorStep.Share, overlay = true))
    }

    @Test
    fun previewSizesStayDistinct() {
        assertEquals(120f, EditorSteps.PREVIEW_COMPACT_DP)
        assertEquals(240f, EditorSteps.PREVIEW_FULL_DP)
    }
}
