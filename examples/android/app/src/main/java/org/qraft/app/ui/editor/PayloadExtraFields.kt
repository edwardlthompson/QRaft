package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.CryptoScheme
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.coreqr.WifiSecurity

@Composable
fun PayloadExtraFields(
    draft: EditorDraft,
    onDraftChange: (EditorDraft) -> Unit,
) {
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
        PayloadKind.Sms, PayloadKind.WhatsApp -> extraField(draft.secondary, R.string.editor_field_body) {
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
        PayloadKind.Calendar -> {
            extraField(draft.secondary, R.string.editor_field_dtstart) {
                onDraftChange(draft.copy(secondary = it))
            }
            extraField(draft.tertiary, R.string.editor_field_location) {
                onDraftChange(draft.copy(tertiary = it))
            }
            extraField(draft.org, R.string.editor_field_body) {
                onDraftChange(draft.copy(org = it))
            }
        }
        PayloadKind.Geo -> extraField(draft.secondary, R.string.editor_field_longitude) {
            onDraftChange(draft.copy(secondary = it))
        }
        PayloadKind.MeCard -> {
            extraField(draft.secondary, R.string.editor_field_phone) {
                onDraftChange(draft.copy(secondary = it))
            }
            extraField(draft.tertiary, R.string.editor_field_email) {
                onDraftChange(draft.copy(tertiary = it))
            }
        }
        PayloadKind.FaceTime -> {
            val mode = draft.secondary.ifBlank { "video" }
            val modeLabels = mapOf(
                "video" to stringResource(R.string.editor_facetime_video),
                "audio" to stringResource(R.string.editor_facetime_audio),
            )
            MenuField(
                label = stringResource(R.string.editor_field_facetime_mode),
                value = mode,
                options = listOf("video", "audio"),
                labelOf = { modeLabels.getValue(it) },
                onSelect = { onDraftChange(draft.copy(secondary = it)) },
            )
        }
        PayloadKind.Social -> {
            val platform = runCatching {
                org.qraft.app.editor.SocialUrls.Platform.valueOf(draft.secondary.ifBlank { "Other" })
            }.getOrDefault(org.qraft.app.editor.SocialUrls.Platform.Other)
            MenuField(
                label = stringResource(R.string.editor_field_social_platform),
                value = platform,
                options = org.qraft.app.editor.SocialUrls.Platform.entries.toList(),
                labelOf = { it.name },
                onSelect = { next ->
                    val expanded = org.qraft.app.editor.SocialUrls.expand(next, draft.primary)
                    onDraftChange(draft.copy(secondary = next.name, primary = expanded))
                },
            )
        }
        PayloadKind.Barcode -> {
            val kind = draft.secondary.ifBlank { "Code128" }
            MenuField(
                label = stringResource(R.string.editor_field_barcode_kind),
                value = kind,
                options = listOf("Code128", "Ean13", "Code39"),
                labelOf = { it },
                onSelect = { onDraftChange(draft.copy(secondary = it)) },
            )
        }
        else -> Unit
    }
}
