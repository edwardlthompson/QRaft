package org.qraft.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class WallpaperBinderTest {
    @Test
    fun flagsMatchTargets() {
        assertEquals(android.app.WallpaperManager.FLAG_SYSTEM, WallpaperBinder.flagsFor(WallpaperTarget.HOME))
        assertEquals(android.app.WallpaperManager.FLAG_LOCK, WallpaperBinder.flagsFor(WallpaperTarget.LOCK))
        assertEquals(
            android.app.WallpaperManager.FLAG_SYSTEM or android.app.WallpaperManager.FLAG_LOCK,
            WallpaperBinder.flagsFor(WallpaperTarget.BOTH),
        )
    }
}
