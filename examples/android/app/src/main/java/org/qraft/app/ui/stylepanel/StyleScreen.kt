package org.qraft.app.ui.stylepanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.QrStyle

@Composable
fun StyleControls(
    style: QrStyle,
    onStyleChange: (QrStyle) -> Unit,
    onPickBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onPickLogo: () -> Unit = {},
    onClearLogo: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        StylePrimaryControls(
            style = style,
            onStyleChange = onStyleChange,
            onPickBackground = onPickBackground,
            onClearBackground = onClearBackground,
            onPickLogo = onPickLogo,
            onClearLogo = onClearLogo,
        )
        StyleAdvancedControls(style = style, onStyleChange = onStyleChange)
    }
}
