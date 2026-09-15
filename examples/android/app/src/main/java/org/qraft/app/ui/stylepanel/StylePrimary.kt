package org.qraft.app.ui.stylepanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.render.CenterMark
import org.qraft.render.CaptionSpec
import org.qraft.render.ModuleShape
import org.qraft.render.QrStyle
import org.qraft.render.StylePresets

private const val PRESET_CUSTOM = "custom"

@Composable
fun StylePrimaryControls(
    style: QrStyle,
    onStyleChange: (QrStyle) -> Unit,
    onPickBackground: () -> Unit = {},
    onClearBackground: () -> Unit = {},
    onPickLogo: () -> Unit = {},
    onClearLogo: () -> Unit = {},
    onDecoratePhoto: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val presetId = StyleLabels.selectedPresetId(style) ?: PRESET_CUSTOM
    val presetIds = buildList {
        if (presetId == PRESET_CUSTOM) add(PRESET_CUSTOM)
        addAll(StylePresets.ALL.map { it.id })
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        MenuField(
            label = stringResource(R.string.style_preset),
            value = presetId,
            options = presetIds,
            labelOf = { stringResource(StyleLabels.presetLabelRes(it)) },
            onSelect = { id ->
                StylePresets.byId(id)?.let { pack ->
                    onStyleChange(
                        pack.style.copy(
                            imageBackgroundPath = style.imageBackgroundPath,
                            logoImagePath = style.logoImagePath,
                            caption = style.caption,
                        ),
                    )
                }
            },
        )
        MenuField(
            label = stringResource(R.string.style_modules),
            value = style.moduleShape,
            options = ModuleShape.entries.toList(),
            labelOf = { stringResource(StyleLabels.moduleLabelRes(it)) },
            onSelect = { onStyleChange(style.copy(moduleShape = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_center),
            value = style.centerMark,
            options = CenterMark.entries.toList(),
            labelOf = { stringResource(StyleLabels.centerLabelRes(it)) },
            onSelect = { mark ->
                onStyleChange(
                    style.copy(
                        centerMark = mark,
                        logoCutout = style.logoCutout.copy(
                            enabled = mark != CenterMark.NONE || style.logoImagePath.isNotBlank(),
                        ),
                    ),
                )
            },
        )
        OutlinedTextField(
            value = style.caption.text,
            onValueChange = { onStyleChange(style.copy(caption = CaptionSpec(it))) },
            label = { Text(stringResource(R.string.editor_caption)) },
            singleLine = true,
        )
        TextButton(onClick = onPickLogo) { Text(stringResource(R.string.style_logo_image)) }
        if (style.logoImagePath.isNotBlank()) {
            TextButton(onClick = onClearLogo) { Text(stringResource(R.string.style_logo_image_clear)) }
        }
        TextButton(onClick = onPickBackground) { Text(stringResource(R.string.style_image_bg)) }
        if (style.imageBackgroundPath.isNotBlank()) {
            TextButton(onClick = onClearBackground) { Text(stringResource(R.string.style_image_bg_clear)) }
        }
        if (onDecoratePhoto != null) {
            TextButton(onClick = onDecoratePhoto) { Text(stringResource(R.string.editor_decorate_photo)) }
        }
    }
}
