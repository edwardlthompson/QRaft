package org.qraft.app.ui.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.editor.ProfileApply
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.editor.EditorActions
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.insets.bottomInsetPadding
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.data.ProfileSearch
import org.qraft.data.ProfileTags
import org.qraft.data.QrProfile

@Composable
fun ProfilesScreen(
    profiles: List<QrProfile>,
    onLoad: (QrProfile) -> Unit,
    onDelete: (String) -> Unit,
    onBackup: (String) -> Unit,
    onRestore: (String) -> Unit,
    onUndo: () -> Unit,
    onExportJson: (QrProfile) -> Unit,
    onExportPng: (QrProfile) -> Unit,
    onExportSvg: (QrProfile) -> Unit,
    onExportPdf: (QrProfile) -> Unit,
    onAddWidget: (QrProfile) -> Unit,
    onUpdate: (QrProfile) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf(ProfileSearch.Sort.NEWEST) }
    var passphrase by rememberSaveable { mutableStateOf("") }
    var openId by rememberSaveable { mutableStateOf<String?>(null) }
    val shown = ProfileSearch.sort(ProfileSearch.filter(profiles, query), sort)
    val open = shown.find { it.id == openId }
    Column(
        modifier = modifier
            .highRefreshScroll()
            .verticalScroll(rememberScrollState())
            .padding(SpacingMd)
            .bottomInsetPadding(),
        verticalArrangement = Arrangement.spacedBy(SpacingMd),
    ) {
        Text(text = stringResource(R.string.nav_profiles), style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(R.string.profiles_search)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        val sortLabels = mapOf(
            ProfileSearch.Sort.NAME to stringResource(R.string.profiles_sort_name),
            ProfileSearch.Sort.NEWEST to stringResource(R.string.profiles_sort_newest),
        )
        MenuField(
            label = stringResource(R.string.profiles_sort),
            value = sort,
            options = ProfileSearch.Sort.entries.toList(),
            labelOf = { sortLabels.getValue(it) },
            onSelect = { sort = it },
        )
        OutlinedTextField(
            value = passphrase,
            onValueChange = { passphrase = it },
            label = { Text(stringResource(R.string.profiles_passphrase)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            TextButton(onClick = { onBackup(passphrase) }) { Text(stringResource(R.string.profiles_backup)) }
            TextButton(onClick = { onRestore(passphrase) }) { Text(stringResource(R.string.profiles_restore)) }
            TextButton(onClick = onUndo) { Text(stringResource(R.string.profiles_undo)) }
        }
        if (open != null) {
            GalleryCard(
                profile = open,
                onEdit = { onLoad(open); openId = null },
                onDelete = { onDelete(open.id); openId = null },
                onExportJson = { onExportJson(open) },
                onExportPng = { onExportPng(open) },
                onExportSvg = { onExportSvg(open) },
                onExportPdf = { onExportPdf(open) },
                onAddWidget = { onAddWidget(open) },
                onUpdate = onUpdate,
                onClose = { openId = null },
            )
        } else if (shown.isEmpty()) {
            Text(
                text = stringResource(R.string.profiles_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            shown.forEach { profile ->
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.clickable { openId = profile.id },
                )
                Text(
                    text = profile.tags.joinToString().ifBlank { profile.payloadText },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { openId = profile.id },
                )
            }
        }
    }
}

@Composable
private fun GalleryCard(
    profile: QrProfile,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onAddWidget: () -> Unit,
    onUpdate: (QrProfile) -> Unit,
    onClose: () -> Unit,
) {
    var name by rememberSaveable(profile.id) { mutableStateOf(profile.name) }
    var tags by rememberSaveable(profile.id) { mutableStateOf(ProfileTags.format(profile.tags)) }
    val applied = ProfileApply.fromProfile(profile)
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(stringResource(R.string.editor_save_name)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = tags,
        onValueChange = { tags = it },
        label = { Text(stringResource(R.string.profiles_tags)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Button(onClick = {
        onUpdate(
            profile.copy(
                name = name.trim().ifBlank { profile.name },
                tags = ProfileTags.parse(tags),
            ),
        )
    }) { Text(stringResource(R.string.profiles_save_meta)) }
    EditorPreview(draft = applied.first, style = applied.second)
    EditorActions(
        canSave = false,
        onSave = {},
        onExportJson = onExportJson,
        onExportPng = onExportPng,
        onExportSvg = onExportSvg,
        onExportPdf = onExportPdf,
        onStyleQr = {},
        onAddWidget = onAddWidget,
        onCurrentWifi = {},
        showSave = false,
        showWifi = false,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
        Button(onClick = onEdit) { Text(stringResource(R.string.profiles_load)) }
        TextButton(onClick = onDelete) { Text(stringResource(R.string.profiles_delete)) }
        TextButton(onClick = onClose) { Text(stringResource(R.string.about_close)) }
    }
}
