package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.CryptoScheme
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.coreqr.WifiSecurity

@Composable
fun PayloadFields(
    draft: EditorDraft,
    onDraftChange: (EditorDraft) -> Unit,
) {
    val primaryLabel = when (draft.kind) {
        PayloadKind.Url -> R.string.home_url_label
        PayloadKind.Text -> R.string.editor_field_text
        PayloadKind.Wifi -> R.string.editor_field_ssid
        PayloadKind.VCard -> R.string.editor_field_name
        PayloadKind.Email -> R.string.editor_field_email
        PayloadKind.Sms, PayloadKind.Phone -> R.string.editor_field_number
        PayloadKind.Crypto -> R.string.editor_field_address
    }
    OutlinedTextField(
        value = draft.primary,
        onValueChange = { onDraftChange(draft.copy(primary = it)) },
        label = { Text(stringResource(primaryLabel)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    when (draft.kind) {
        PayloadKind.Wifi -> {
            OutlinedTextField(
                value = draft.secondary,
                onValueChange = { onDraftChange(draft.copy(secondary = it)) },
                label = { Text(stringResource(R.string.editor_field_password)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            MenuField(
                label = stringResource(R.string.editor_field_wifi_security),
                value = draft.wifiSecurity,
                options = WifiSecurity.entries.toList(),
                labelOf = { it.name },
                onSelect = { onDraftChange(draft.copy(wifiSecurity = it)) },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = draft.wifiHidden,
                    onCheckedChange = { onDraftChange(draft.copy(wifiHidden = it)) },
                )
                Text(text = stringResource(R.string.editor_wifi_hidden))
            }
        }
        PayloadKind.VCard -> {
            extraField(draft.secondary, R.string.editor_field_phone) {
                onDraftChange(draft.copy(secondary = it))
            }
            extraField(draft.tertiary, R.string.editor_field_email) {
                onDraftChange(draft.copy(tertiary = it))
            }
            extraField(draft.org, R.string.editor_field_org) {
                onDraftChange(draft.copy(org = it))
            }
            extraField(draft.url, R.string.editor_field_url) {
                onDraftChange(draft.copy(url = it))
            }
        }
        PayloadKind.Email -> {
            extraField(draft.secondary, R.string.editor_field_subject) {
                onDraftChange(draft.copy(secondary = it))
            }
            extraField(draft.tertiary, R.string.editor_field_body) {
                onDraftChange(draft.copy(tertiary = it))
            }
        }
        PayloadKind.Sms -> extraField(draft.secondary, R.string.editor_field_body) {
            onDraftChange(draft.copy(secondary = it))
        }
        PayloadKind.Crypto -> {
            val schemeLabels = CryptoScheme.entries.associateWith { scheme ->
                stringResource(
                    when (scheme) {
                        CryptoScheme.None -> R.string.crypto_scheme_none
                        CryptoScheme.Bitcoin -> R.string.crypto_scheme_bitcoin
                        CryptoScheme.Ethereum -> R.string.crypto_scheme_ethereum
                        CryptoScheme.Litecoin -> R.string.crypto_scheme_litecoin
                        CryptoScheme.Dogecoin -> R.string.crypto_scheme_dogecoin
                        CryptoScheme.Monero -> R.string.crypto_scheme_monero
                    },
                )
            }
            MenuField(
                label = stringResource(R.string.editor_field_scheme),
                value = CryptoScheme.fromWire(draft.secondary),
                options = CryptoScheme.entries.toList(),
                labelOf = { schemeLabels.getValue(it) },
                onSelect = { onDraftChange(draft.copy(secondary = it.wire)) },
            )
        }
        else -> Unit
    }
}

@Composable
private fun extraField(value: String, label: Int, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
