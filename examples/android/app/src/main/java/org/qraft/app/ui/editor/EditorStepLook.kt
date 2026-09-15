package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import org.qraft.app.R
import org.qraft.app.ui.stylepanel.StyleAdvancedControls
import org.qraft.app.ui.stylepanel.StylePrimaryControls
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.QrStyle

@Composable
internal fun EditorStepLook(
    style: QrStyle,
    onStyleChange: (QrStyle) -> Unit,
    onPickBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onPickLogo: () -> Unit,
    onClearLogo: () -> Unit,
    onDecoratePhoto: () -> Unit,
    advancedOpen: Boolean,
    onToggleAdvanced: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val advancedState = stringResource(
        if (advancedOpen) R.string.editor_expanded else R.string.editor_collapsed,
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        StylePrimaryControls(
            style = style,
            onStyleChange = onStyleChange,
            onPickBackground = onPickBackground,
            onClearBackground = onClearBackground,
            onPickLogo = onPickLogo,
            onClearLogo = onClearLogo,
            onDecoratePhoto = onDecoratePhoto,
        )
        TextButton(
            onClick = onToggleAdvanced,
            modifier = Modifier.semantics { stateDescription = advancedState },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.editor_step_advanced))
                Icon(
                    if (advancedOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                )
            }
        }
        if (advancedOpen) {
            StyleAdvancedControls(style = style, onStyleChange = onStyleChange)
        }
    }
}
