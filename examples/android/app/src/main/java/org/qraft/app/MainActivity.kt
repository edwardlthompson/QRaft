package org.qraft.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import org.qraft.app.crashcapture.CrashCapture
import org.qraft.app.about.AppUpdatePreferences
import org.qraft.app.display.WindowRefresh
import org.qraft.app.network.NetworkStatusMonitor
import org.qraft.app.ui.QRaftApp
import org.qraft.app.ui.theme.ThemePreferences
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var networkStatusMonitor: NetworkStatusMonitor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CrashCapture.install(applicationContext)
        val themePreferences = ThemePreferences(applicationContext)
        val appUpdatePreferences = AppUpdatePreferences(applicationContext)
        networkStatusMonitor = NetworkStatusMonitor(applicationContext).also { it.start() }

        lifecycleScope.launch {
            appUpdatePreferences.clearPendingRestart()
            appUpdatePreferences.ensureInstalledFormat()
        }

        setContent {
            QRaftApp(
                context = this,
                scope = lifecycleScope,
                themePreferences = themePreferences,
                appUpdatePreferences = appUpdatePreferences,
                networkStatusMonitor = networkStatusMonitor!!,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        WindowRefresh.applyTo(this)
    }

    override fun onDestroy() {
        networkStatusMonitor?.stop()
        super.onDestroy()
    }
}
