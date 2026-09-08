package org.qraft.app.about

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class ReleaseTagFetcherTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun loadReleaseRepoReadsConfiguredRepo() {
        assertEquals("edwardlthompson/QRaft", ReleaseTagFetcher.loadReleaseRepo(context))
    }

    @Test
    fun loadProductAssetPrefixDefaultsToProductPrefix() {
        assertEquals(ProductUpdate.DEFAULT_ASSET_PREFIX, ReleaseTagFetcher.loadProductAssetPrefix(context))
    }

    @Test
    fun manifestHasNoInternetPermission() {
        val info = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS,
        )
        val perms = info.requestedPermissions?.toList() ?: emptyList()
        assertFalse(
            "QRaft is offline-only; INTERNET must not be declared",
            perms.contains("android.permission.INTERNET"),
        )
    }

    @Test
    fun fetchLatestReleaseReturnsNullForInvalidRepo() {
        val result = kotlinx.coroutines.runBlocking {
            ReleaseTagFetcher.fetchLatestRelease("invalid/empty-repo-404")
        }
        assertNull(result)
    }
}
