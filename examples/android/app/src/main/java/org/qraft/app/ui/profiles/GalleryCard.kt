package org.qraft.app.ui.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.app.editor.ProfileApply
import org.qraft.app.share.ExportPngSize
import org.qraft.app.ui.editor.EditorActions
import org.qraft.app.ui.editor.EditorPreview
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.editor.PayloadFields
import org.qraft.app.ui.editor.PayloadKindIcons
import org.qraft.app.ui.stylepanel.StyleControls
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.data.ProfileTags
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson

@Composable
internal fun GalleryCard(
    profile: QrProfile,
    onSaveEdit: (name: String, tags: String, draft: EditorDraft, style: QrStyle) -> Unit,
    onEditOnHome: () -> Unit,
    onDelete: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onAddWidget: () -> Unit,
    onSetWallpaper: () -> Unit,
    onCopyPayload: () -> Unit,
    onPrint: () -> Unit,
    onZip: () -> Unit,
    onPngSize: (ExportPngSize) -> Unit,
    pngSize: ExportPngSize,
    onClose: () -> Unit,
) {
    val seed = ProfileApply.fromProfile(profile)
    var name by rememberSaveable(profile.id) { mutableStateOf(profile.name) }
    var tags by rememberSaveable(profile.id) { mutableStateOf(ProfileTags.format(profile.tags)) }
    var draft by remember(profile.id) { mutableStateOf(seed.first) }
    var styleJson by remember(profile.id) { mutableStateOf(QrStyleJson.encode(seed.second)) }
    val style = QrStyleJson.decode(styleJson)
    val canSave = draft.toPayload() != null
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        EditorPreview(draft = draft, style = style)
        val kindLabels = PayloadKind.entries.associateWith { stringResource(kindLabel(it)) }
        MenuField(
            label = stringResource(R.string.editor_kind),
            value = draft.kind,
            options = PayloadKind.entries.toList(),
            labelOf = { kindLabels.getValue(it) },
            iconOf = { PayloadKindIcons.of(it) },
            onSelect = { draft = EditorDraft.withKind(it) },
        )
        PayloadFields(draft = draft, onDraftChange = { draft = it })
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
        StyleControls(
            style = style,
            onStyleChange = { styleJson = QrStyleJson.encode(it) },
            onPickBackground = {},
            onClearBackground = {
                styleJson = QrStyleJson.encode(style.copy(imageBackgroundPath = ""))
            },
        )
        Button(
            onClick = { onSaveEdit(name, tags, draft, style) },
            enabled = canSave,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.profiles_save_edits)) }
        EditorActions(
            canSave = false,
            onSave = {},
            onExportJson = onExportJson,
            onExportPng = onExportPng,
            onExportSvg = onExportSvg,
            onExportPdf = onExportPdf,
            onStyleQr = {},
            onAddWidget = onAddWidget,
            onCopyPayload = onCopyPayload,
            onPrint = onPrint,
            onZip = onZip,
            onSetWallpaper = onSetWallpaper,
            onPngSize = onPngSize,
            pngSize = pngSize,
            onCurrentWifi = {},
            showSave = false,
            showWifi = false,
            showGalleryExtras = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(SpacingMd)) {
            Button(onClick = onEditOnHome) { Text(stringResource(R.string.profiles_edit_home)) }
            TextButton(onClick = onDelete) { Text(stringResource(R.string.profiles_delete)) }
            TextButton(onClick = onClose) { Text(stringResource(R.string.about_close)) }
        }
    }
}

private fun kindLabel(kind: PayloadKind): Int = when (kind) {
    PayloadKind.Url -> R.string.editor_kind_url
    PayloadKind.Text -> R.string.editor_kind_text
    PayloadKind.Wifi -> R.string.editor_kind_wifi
    PayloadKind.VCard -> R.string.editor_kind_vcard
    PayloadKind.Email -> R.string.editor_kind_email
    PayloadKind.Sms -> R.string.editor_kind_sms
    PayloadKind.Phone -> R.string.editor_kind_phone
    PayloadKind.Crypto -> R.string.editor_kind_crypto
    PayloadKind.Calendar -> R.string.editor_kind_calendar
    PayloadKind.Geo -> R.string.editor_kind_geo
    PayloadKind.WhatsApp -> R.string.editor_kind_whatsapp
    PayloadKind.AppStore -> R.string.editor_kind_appstore
    PayloadKind.Social -> R.string.editor_kind_social
    PayloadKind.MeCard -> R.string.editor_kind_mecard
    PayloadKind.FaceTime -> R.string.editor_kind_facetime
    PayloadKind.Barcode -> R.string.editor_kind_barcode
}
