package org.qraft.app.ui.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.qraft.app.R
import org.qraft.app.ui.theme.SpacingMd

@Composable
internal fun VaultPassphraseRow(
    passphrase: String,
    onPassphrase: (String) -> Unit,
    vaultBusy: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        OutlinedTextField(
            value = passphrase,
            onValueChange = onPassphrase,
            enabled = !vaultBusy,
            label = { Text(stringResource(R.string.profiles_passphrase)) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.profiles_vault_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
