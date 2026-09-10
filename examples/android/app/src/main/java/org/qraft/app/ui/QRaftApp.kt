package org.qraft.app.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.SnackbarHostState
import org.qraft.app.BuildConfig
import org.qraft.app.R
import org.qraft.app.crashcapture.PendingCrashStore
import org.qraft.app.feedback.FeedbackPrefs
import org.qraft.app.settings.ProductPrefs
import org.qraft.app.about.ReleaseTagFetcher
import org.qraft.app.about.AppUpdatePreferences
import org.qraft.app.about.AppUpdates
import org.qraft.app.about.DonationsLoader
import org.qraft.app.about.UpdateLaunchPrefs
import org.qraft.app.network.NetworkStatusMonitor
import org.qraft.app.ui.insets.NavigationModeProvider
import org.qraft.app.ui.nav.GpRoute
import org.qraft.app.ui.nav.Nav
import org.qraft.app.ui.nav.NavBack
import org.qraft.app.ui.nav.NavPreferences
import org.qraft.app.ui.nav.NavSession
import org.qraft.app.ui.nav.NavState
import org.qraft.app.ui.nav.NavStore
import org.qraft.app.ui.theme.ThemeMode
import org.qraft.app.ui.theme.ThemePreferences
import org.qraft.app.ui.theme.next
import kotlinx.coroutines.CoroutineScope
import org.qraft.app.ui.chrome.QRaftScreen
import org.qraft.app.ui.theme.QRaftTheme
import kotlinx.coroutines.launch

private val NavStateSaver = Saver<NavState, String>(
    save = { NavStore.save(it) },
    restore = { NavStore.load(it) },
)

@Composable
fun QRaftApp(
    context: Context,
    scope: CoroutineScope,
    themePreferences: ThemePreferences,
    appUpdatePreferences: AppUpdatePreferences,
    @Suppress("UNUSED_PARAMETER") networkStatusMonitor: NetworkStatusMonitor,
) {
    val themeMode by themePreferences.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.System)
    val installedFormat by appUpdatePreferences.installedFormat.collectAsStateWithLifecycle(initialValue = "apk")
    val pendingRestart by appUpdatePreferences.pendingRestart.collectAsStateWithLifecycle(initialValue = false)
    val navPrefs = remember { NavPreferences(context) }
    val crashStore = remember { PendingCrashStore(context) }
    var nav by rememberSaveable(stateSaver = NavStateSaver) {
        mutableStateOf(NavSession.boot(navPrefs.read(), crashStore.read() != null))
    }
    val scrollRef = remember { mutableMapOf<GpRoute, Int>() }
    val feedbackPrefs = remember { FeedbackPrefs(context) }
    val productPrefs = remember { ProductPrefs(context) }
    var saveCrashes by remember { mutableStateOf(feedbackPrefs.saveCrashes()) }
    var nudgePrompts by remember { mutableStateOf(productPrefs.nudgePrompts()) }
    var updateStatus by remember { mutableStateOf(context.getString(R.string.about_update_current)) }
    var launchPrompt by remember { mutableStateOf<AppUpdates.LaunchPrompt?>(null) }
    val donations = remember { DonationsLoader.load(context) }
    val appVersion = BuildConfig.VERSION_NAME
    val snackbarHostState = remember { SnackbarHostState() }
    val launchPrefs = remember { UpdateLaunchPrefs(context) }

    fun applyNav(next: NavState) {
        var merged = next
        scrollRef.forEach { (route, y) -> merged = Nav.recordScroll(merged, route, y) }
        nav = merged
        navPrefs.write(merged)
    }

    fun popNav() {
        val result = NavBack.onSystemBack(nav)
        applyNav(result.next)
        if (!result.next.promptOpen) launchPrompt = null
    }

    BackHandler(enabled = true) { popNav() }

    LaunchedEffect(pendingRestart) {
        if (pendingRestart) updateStatus = context.getString(R.string.about_update_restarting)
    }

    LaunchedEffect(Unit) {
        navPrefs.write(nav)
        if (productPrefs.nudgePrompts()) {
            val prompt = AppUpdates.onLaunch(context, appVersion)
            launchPrompt = prompt
            if (prompt != null) applyNav(Nav.setPrompt(nav, true))
        }
    }

    QRaftTheme(themeMode = themeMode) {
        NavigationModeProvider {
            QRaftScreen(
                snackbarHostState = snackbarHostState,
                themeMode = themeMode,
                nav = nav,
                saveCrashes = saveCrashes,
                nudgePrompts = nudgePrompts,
                releaseRepo = ReleaseTagFetcher.loadReleaseRepo(context).orEmpty(),
                pendingStack = crashStore.read()?.stack,
                appVersion = appVersion,
                installedFormat = installedFormat ?: "apk",
                updateStatus = updateStatus,
                donations = donations,
                canApplyUpdate = false,
                launchPrompt = launchPrompt,
                onThemeToggle = { scope.launch { themePreferences.setThemeMode(themeMode.next()) } },
                onThemeModeSelect = { mode -> scope.launch { themePreferences.setThemeMode(mode) } },
                onPushRoute = { route, kind -> applyNav(Nav.push(nav, route, kind)) },
                onPop = { popNav() },
                onScroll = { route, y -> scrollRef[route] = y },
                onSaveCrashes = { on -> feedbackPrefs.setSaveCrashes(on); saveCrashes = on },
                onNudgePrompts = { on -> productPrefs.setNudgePrompts(on); nudgePrompts = on },
                onFeedbackClose = { crashStore.clear(); popNav() },
                onDonatePrompt = { donate ->
                    handleDonatePrompt(
                        donate, appVersion, donations, launchPrefs, nav, context, ::applyNav,
                    ) { launchPrompt = null }
                },
                onUpdatePrompt = { install ->
                    handleUpdatePrompt(
                        install, launchPrompt, launchPrefs, nav, context, ::applyNav,
                    ) { launchPrompt = null }
                },
                onApplyUpdate = {},
            )
        }
    }
}
