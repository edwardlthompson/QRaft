package org.qraft.app.ui.tour

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.qraft.app.R

class TourStepsTest {
    @Test
    fun namesScanAndWallpaperAndStaysSmall() {
        assertEquals(5, TourSteps.BODY_RES.size)
        assertTrue(TourSteps.BODY_RES.contains(R.string.tour_scan))
        assertTrue(TourSteps.BODY_RES.contains(R.string.tour_wallpaper))
        assertTrue(TourSteps.BODY_RES.contains(R.string.tour_widget))
    }
}
