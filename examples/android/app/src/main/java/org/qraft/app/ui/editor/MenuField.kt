package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import org.qraft.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MenuField(
    label: String,
    value: T,
    options: List<T>,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    iconOf: ((T) -> ImageVector)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selected = labelOf(value)
    val talkback = stringResource(R.string.editor_menu_cd, label, selected)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = iconOf?.let { icons ->
                { Icon(icons(value), contentDescription = null) }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .semantics { contentDescription = talkback },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                val optionLabel = labelOf(option)
                val optionCd = stringResource(R.string.editor_menu_cd, label, optionLabel)
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    leadingIcon = iconOf?.let { icons ->
                        { Icon(icons(option), contentDescription = null) }
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    modifier = Modifier.semantics { contentDescription = optionCd },
                )
            }
        }
    }
}
