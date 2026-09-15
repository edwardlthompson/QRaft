package org.qraft.app.ui.stylepanel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.app.R
import org.qraft.coreqr.WifiSecurity
import org.qraft.render.CenterMark
import org.qraft.render.ColorTheme
import org.qraft.render.FinderShape
import org.qraft.render.ModuleShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle
import org.qraft.render.StylePresets

class StyleLabelsTest {
    @Test
    fun everyModuleHasALabel() {
        ModuleShape.entries.forEach { assertTrue(StyleLabels.moduleLabelRes(it) != 0) }
    }

    @Test
    fun everyThemeHasALabel() {
        ColorTheme.entries.forEach { assertTrue(StyleLabels.themeLabelRes(it) != 0) }
    }

    @Test
    fun everyFinderHasALabel() {
        FinderShape.entries.forEach { assertTrue(StyleLabels.finderLabelRes(it) != 0) }
    }

    @Test
    fun everyFrameHasALabel() {
        QrFrame.entries.forEach { assertTrue(StyleLabels.frameLabelRes(it) != 0) }
    }

    @Test
    fun everyCenterHasALabel() {
        CenterMark.entries.forEach { assertTrue(StyleLabels.centerLabelRes(it) != 0) }
    }

    @Test
    fun wifiSecurityUsesWords() {
        assertEquals(R.string.style_wifi_open, StyleLabels.wifiSecurityLabelRes(WifiSecurity.NOPASS))
        assertEquals(R.string.style_wifi_wpa, StyleLabels.wifiSecurityLabelRes(WifiSecurity.WPA))
    }

    @Test
    fun defaultStyleIsClassicPreset() {
        assertEquals("classic", StyleLabels.selectedPresetId(QrStyle.DEFAULT))
        assertEquals("classic", StyleLabels.selectedPresetId(StylePresets.byId("classic")!!.style))
    }

    @Test
    fun mixedStyleIsCustom() {
        assertNull(
            StyleLabels.selectedPresetId(
                QrStyle.DEFAULT.copy(moduleShape = ModuleShape.BLOB, colorTheme = ColorTheme.BLUE),
            ),
        )
        assertEquals(R.string.style_preset_custom, StyleLabels.presetLabelRes("nope"))
    }

    @Test
    fun finderFoldSetsPupil() {
        val next = StyleLookFold.withFinder(QrStyle.DEFAULT, FinderShape.RING)
        assertEquals(FinderShape.RING, next.finderShape)
        assertEquals(FinderShape.RING, next.finderPupil)
    }
}
