package org.qraft.app.settings

import org.junit.Assert.assertFalse
import org.junit.Test

class ProductPrefsTest {
    @Test
    fun launchNudgesDefaultOff() {
        assertFalse(ProductPrefs.DEFAULT)
    }
}
