package org.qraft.app.ui.chrome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import org.qraft.app.R
import org.qraft.app.about.AppUpdates
import org.qraft.app.about.DonationsConfig
import org.qraft.app.ui.about.AboutScreen
import org.qraft.app.ui.about.LaunchPromptDialogs
import org.qraft.app.ui.components.BrandMark
import org.qraft.app.ui.components.ProductNavBar
import org.qraft.app.ui.components.QRaftScaffold
import org.qraft.app.ui.feedback.FeedbackScreen
import org.qraft.app.ui.nav.FeedbackKind
import org.qraft.app.ui.nav.GpRoute
import org.qraft.app.ui.nav.Nav
import org.qraft.app.ui.nav.NavState
import org.qraft.app.ui.product.ProductPages
import org.qraft.app.ui.profiles.GalleryChromeState
import org.qraft.app.ui.settings.SettingsScreen
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRaftScreen(
    snackbarHostState: SnackbarHostState,
    themeMode: ThemeMode,
    dynamicColor: Boolean = false,
    nav: NavState,
    saveCrashes: Boolean,
    nudgePrompts: Boolean,
    releaseRepo: String,
    pendingStack: String?,
    appVersion: String,
    installedFormat: String,
    updateStatus: String,
    donations: DonationsConfig,
    canApplyUpdate: Boolean,
    launchPrompt: AppUpdates.LaunchPrompt?,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onDynamicColor: (Boolean) -> Unit = {},
    onPushRoute: (GpRoute, FeedbackKind?) -> Unit,
    onPop: () -> Unit,
    onScroll: (GpRoute, Int) -> Unit,
    onSaveCrashes: (Boolean) -> Unit,
    onNudgePrompts: (Boolean) -> Unit,
    onFeedbackClose: () -> Unit,
    onDonatePrompt: (Boolean) -> Unit,
    onUpdatePrompt: (Boolean) -> Unit,
    onApplyUpdate: () -> Unit,
) {
    val route = Nav.current(nav)
    val productTabs = route == GpRoute.Home ||
        route == GpRoute.Profiles ||
        route == GpRoute.Scan ||
        route == GpRoute.Wallpaper
    val galleryChrome = remember { GalleryChromeState() }
    LaunchedEffect(route) {
        if (route != GpRoute.Profiles) galleryChrome.collapse()
    }
    QRaftScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        BrandMark(size = 28.dp)
                        Text(stringResource(R.string.app_title))
                    }
                },
                actions = {
                    if (route == GpRoute.Profiles) {
                        val expanded = stringResource(R.string.editor_expanded)
                        val collapsed = stringResource(R.string.editor_collapsed)
                        FilledTonalIconToggleButton(
                            checked = galleryChrome.searchOpen,
                            onCheckedChange = { galleryChrome.toggleSearch() },
                            modifier = Modifier
                                .heightIn(min = MinTouchDp)
                                .semantics {
                                    stateDescription = ChromeSelected.stateDescription(
                                        galleryChrome.searchOpen,
                                        expanded,
                                        collapsed,
                                    )
                                },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(R.string.profiles_search_open),
                            )
                        }
                        FilledTonalIconToggleButton(
                            checked = galleryChrome.filterOpen,
                            onCheckedChange = { galleryChrome.toggleFilter() },
                            modifier = Modifier
                                .heightIn(min = MinTouchDp)
                                .semantics {
                                    stateDescription = ChromeSelected.stateDescription(
                                        galleryChrome.filterOpen,
                                        expanded,
                                        collapsed,
                                    )
                                },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = stringResource(R.string.profiles_filter_open),
                            )
                        }
                    }
                    IconButton(onClick = { toggleRoute(route, GpRoute.Settings, onPushRoute, onPop) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_open),
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (productTabs) {
                ProductNavBar(current = route, onSelect = { dest -> onPushRoute(dest, null) })
            }
        },
    ) { innerPadding ->
        if (nav.promptOpen && launchPrompt != null) {
            LaunchPromptDialogs(
                prompt = launchPrompt,
                onDonate = onDonatePrompt,
                onUpdate = onUpdatePrompt,
            )
        }
        val panelMod = Modifier.fillMaxSize().padding(innerPadding)
        when (route) {
            GpRoute.Feedback -> FeedbackScreen(
                kind = nav.feedbackKind?.wire ?: FeedbackKind.Bug.wire,
                releaseRepo = releaseRepo,
                stack = pendingStack,
                onBack = onFeedbackClose,
                scrollY = Nav.restoreScroll(nav, GpRoute.Feedback),
                onScroll = { onScroll(GpRoute.Feedback, it) },
                modifier = panelMod,
            )
            GpRoute.Settings -> SettingsScreen(
                themeMode = themeMode,
                onThemeModeSelect = onThemeModeSelect,
                dynamicColor = dynamicColor,
                onDynamicColor = onDynamicColor,
                saveCrashes = saveCrashes,
                onSaveCrashes = onSaveCrashes,
                nudgePrompts = nudgePrompts,
                onNudgePrompts = onNudgePrompts,
                onOpenAbout = { onPushRoute(GpRoute.About, null) },
                onBack = onPop,
                scrollY = Nav.restoreScroll(nav, GpRoute.Settings),
                onScroll = { onScroll(GpRoute.Settings, it) },
                modifier = panelMod,
            )
            GpRoute.About -> AboutScreen(
                version = appVersion,
                installedFormat = installedFormat,
                updateStatus = updateStatus,
                donations = donations,
                canApplyUpdate = canApplyUpdate,
                onApplyUpdate = onApplyUpdate,
                onReportBug = { onPushRoute(GpRoute.Feedback, FeedbackKind.Bug) },
                onRequestFeature = { onPushRoute(GpRoute.Feedback, FeedbackKind.Feature) },
                onBack = onPop,
                scrollY = Nav.restoreScroll(nav, GpRoute.About),
                onScroll = { onScroll(GpRoute.About, it) },
                modifier = panelMod,
            )
            GpRoute.Scan, GpRoute.Home, GpRoute.Profiles, GpRoute.Wallpaper ->
                ProductPages(
                    route = route,
                    snackbarHostState = snackbarHostState,
                    modifier = panelMod,
                    galleryChrome = galleryChrome,
                    onOpenGallery = { onPushRoute(GpRoute.Profiles, null) },
                    onOpenHome = { onPushRoute(GpRoute.Home, null) },
                )
            GpRoute.Style ->
                ProductPages(
                    route = GpRoute.Home,
                    snackbarHostState = snackbarHostState,
                    modifier = panelMod,
                    galleryChrome = galleryChrome,
                    onOpenGallery = { onPushRoute(GpRoute.Profiles, null) },
                    onOpenHome = { onPushRoute(GpRoute.Home, null) },
                )
        }
    }
}

private fun toggleRoute(
    current: GpRoute,
    target: GpRoute,
    onPushRoute: (GpRoute, FeedbackKind?) -> Unit,
    onPop: () -> Unit,
) {
    if (current == target) onPop() else onPushRoute(target, null)
}
