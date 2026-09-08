package org.qraft.app.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.qraft.app.about.AppUpdates
import org.qraft.app.about.DonationsConfig
import org.qraft.app.about.DonationsLoader
import org.qraft.app.about.UpdateLaunchPrefs
import org.qraft.app.ui.nav.Nav
import org.qraft.app.ui.nav.NavState

internal fun openExternalUrl(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

internal fun handleDonatePrompt(
    donate: Boolean,
    appVersion: String,
    donations: DonationsConfig,
    launchPrefs: UpdateLaunchPrefs,
    nav: NavState,
    context: Context,
    applyNav: (NavState) -> Unit,
    clearPrompt: () -> Unit,
) {
    launchPrefs.markVersionSeen(appVersion)
    clearPrompt()
    applyNav(Nav.setPrompt(nav, false))
    if (donate) openExternalUrl(context, DonationsLoader.primaryUrl(donations))
}

internal fun handleUpdatePrompt(
    install: Boolean,
    launchPrompt: AppUpdates.LaunchPrompt?,
    launchPrefs: UpdateLaunchPrefs,
    nav: NavState,
    context: Context,
    applyNav: (NavState) -> Unit,
    clearPrompt: () -> Unit,
) {
    val prompt = launchPrompt as? AppUpdates.LaunchPrompt.Update
    clearPrompt()
    applyNav(Nav.setPrompt(nav, false))
    if (prompt != null) {
        launchPrefs.markChecked(System.currentTimeMillis(), prompt.version)
        if (install) openExternalUrl(context, prompt.url)
    }
}
