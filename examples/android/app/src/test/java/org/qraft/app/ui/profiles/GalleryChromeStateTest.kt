package org.qraft.app.ui.profiles

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.data.ProfileSearch

class GalleryChromeStateTest {
    @Test
    fun toggleSearchAndFilterAndCollapse() {
        val chrome = GalleryChromeState()
        chrome.toggleSearch()
        assertTrue(chrome.searchOpen)
        chrome.toggleFilter()
        assertTrue(chrome.filterOpen)
        chrome.query = "wifi"
        chrome.sort = ProfileSearch.Sort.NAME
        chrome.collapse()
        assertFalse(chrome.searchOpen)
        assertFalse(chrome.filterOpen)
        assertEquals("wifi", chrome.query)
        assertEquals(ProfileSearch.Sort.NAME, chrome.sort)
    }
}
