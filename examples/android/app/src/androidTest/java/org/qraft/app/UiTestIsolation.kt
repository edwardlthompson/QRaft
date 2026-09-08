package org.qraft.app

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.qraft.app.about.UpdateLaunchPrefs
import org.qraft.app.ui.nav.NavStore
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** Clears persisted nav / launch prefs before each instrumented test launches the Activity. */
class ClearUiPrefsRule : TestWatcher() {
    override fun starting(description: Description) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences(NavStore.PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences(UpdateLaunchPrefs.PREFS, Context.MODE_PRIVATE).edit().clear().commit()
    }
}

fun ComposeTestRule.dismissLaunchPrompts() {
    waitForIdle()
    for (label in listOf("Not now", "Later")) {
        if (onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty()) {
            onAllNodesWithText(label)[0].performClick()
            waitForIdle()
        }
    }
}
