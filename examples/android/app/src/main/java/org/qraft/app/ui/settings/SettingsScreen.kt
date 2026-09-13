package org.qraft.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.insets.bottomInsetPadding
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.app.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeSelect: (ThemeMode) -> Unit,
    dynamicColor: Boolean = false,
    onDynamicColor: (Boolean) -> Unit = {},
    saveCrashes: Boolean,
    onSaveCrashes: (Boolean) -> Unit,
    nudgePrompts: Boolean = false,
    onNudgePrompts: (Boolean) -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onBack: () -> Unit,
    scrollY: Int = 0,
    onScroll: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState(initial = scrollY)
    LaunchedEffect(scrollState.value) { onScroll(scrollState.value) }
    val themeLabels = mapOf(
        ThemeMode.System to stringResource(R.string.settings_theme_mode_system),
        ThemeMode.Light to stringResource(R.string.settings_theme_mode_light),
        ThemeMode.Dark to stringResource(R.string.settings_theme_mode_dark),
    )
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
        MenuField(
            label = stringResource(R.string.settings_theme_label),
            value = themeMode,
            options = ThemeMode.entries.toList(),
            labelOf = { themeLabels.getValue(it) },
            onSelect = onThemeModeSelect,
        )
        Text(text = stringResource(R.string.settings_dynamic_color))
        Switch(checked = dynamicColor, onCheckedChange = onDynamicColor)
        Text(
            text = stringResource(R.string.settings_dynamic_color_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = stringResource(R.string.settings_feedback_save_crashes))
        Switch(checked = saveCrashes, onCheckedChange = onSaveCrashes)
        Text(text = stringResource(R.string.settings_nudge_prompts))
        Switch(checked = nudgePrompts, onCheckedChange = onNudgePrompts)
        Button(onClick = onOpenAbout) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.settings_about))
            }
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
