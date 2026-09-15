package org.qraft.app.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CopyHonestyTest {
    @Test
    fun scanStatusHiddenWhenSameAsHint() {
        val hint = "Point at a code. It scans by itself."
        assertFalse(CopyHonesty.showScanStatus("", hint))
        assertFalse(CopyHonesty.showScanStatus(hint, hint))
        assertTrue(CopyHonesty.showScanStatus("Code found", hint))
    }

    @Test
    fun aboutNavDebugOnlyOnDebugBuild() {
        assertFalse(CopyHonesty.showAboutNavDebug(false))
        assertTrue(CopyHonesty.showAboutNavDebug(true))
    }
}
