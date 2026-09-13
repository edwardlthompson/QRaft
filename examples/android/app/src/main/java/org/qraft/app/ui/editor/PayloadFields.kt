package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind

@Composable
fun PayloadFields(
    draft: EditorDraft,
    onDraftChange: (EditorDraft) -> Unit,
) {
    if (draft.kind == PayloadKind.VCard) {
        OutlinedTextField(
            value = draft.givenName,
            onValueChange = { onDraftChange(draft.copy(givenName = it, primary = it)) },
            label = { Text(stringResource(R.string.editor_field_given_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = draft.familyName,
            onValueChange = { onDraftChange(draft.copy(familyName = it)) },
            label = { Text(stringResource(R.string.editor_field_family_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        PayloadExtraFields(draft = draft, onDraftChange = onDraftChange)
        return
    }
    val primaryLabel = when (draft.kind) {
        PayloadKind.Url -> R.string.home_url_label
        PayloadKind.Text -> R.string.editor_field_text
        PayloadKind.Wifi -> R.string.editor_field_ssid
        PayloadKind.VCard -> R.string.editor_field_name
        PayloadKind.Email -> R.string.editor_field_email
        PayloadKind.Sms, PayloadKind.Phone, PayloadKind.WhatsApp -> R.string.editor_field_number
        PayloadKind.Crypto -> R.string.editor_field_address
        PayloadKind.Calendar -> R.string.editor_field_summary
        PayloadKind.Geo -> R.string.editor_field_latitude
        PayloadKind.AppStore, PayloadKind.Social -> R.string.editor_field_url
        PayloadKind.MeCard -> R.string.editor_field_name
        PayloadKind.FaceTime -> R.string.editor_field_email
        PayloadKind.Barcode -> R.string.editor_field_barcode
    }
    OutlinedTextField(
        value = draft.primary,
        onValueChange = { onDraftChange(draft.copy(primary = it)) },
        label = { Text(stringResource(primaryLabel)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    PayloadExtraFields(draft = draft, onDraftChange = onDraftChange)
}

@Composable
internal fun extraField(value: String, label: Int, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
