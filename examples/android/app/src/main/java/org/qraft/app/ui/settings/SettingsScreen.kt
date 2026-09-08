package org.qraft.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.ui.insets.bottomInsetPadding
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.app.ui.theme.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    saveCrashes: Boolean,
    onSaveCrashes: (Boolean) -> Unit,
    onOpenAbout: () -> Unit = {},
    onBack: () -> Unit,
    scrollY: Int = 0,
    onScroll: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState(initial = scrollY)
    LaunchedEffect(scrollState.value) { onScroll(scrollState.value) }
    Column(
        modifier = modifier
            .highRefreshScroll()
            .verticalScroll(scrollState)
            .padding(SpacingMd),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(text = stringResource(R.string.settings_theme_label))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = themeMode == mode,
                    onClick = { onThemeModeSelect(mode) },
                    label = {
                        Text(
                            when (mode) {
                                ThemeMode.System -> stringResource(R.string.settings_theme_mode_system)
                                ThemeMode.Light -> stringResource(R.string.settings_theme_mode_light)
                                ThemeMode.Dark -> stringResource(R.string.settings_theme_mode_dark)
                            },
                        )
                    },
                )
            }
        }
        Text(text = stringResource(R.string.settings_feedback_save_crashes))
        Switch(checked = saveCrashes, onCheckedChange = onSaveCrashes)
        Button(onClick = onOpenAbout) {
            Text(stringResource(R.string.settings_about))
        }
        Text(
            text = stringResource(R.string.settings_about_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = onBack,
            modifier = Modifier.bottomInsetPadding(),
        ) {
            Text(stringResource(R.string.settings_close))
        }
    }
}
