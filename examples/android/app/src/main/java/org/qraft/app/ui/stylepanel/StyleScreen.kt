package org.qraft.app.ui.stylepanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.coreqr.Scannability
import org.qraft.render.CaptionSpec
import org.qraft.render.CenterMark
import org.qraft.render.ColorTheme
import org.qraft.render.ColorThemes
import org.qraft.render.FinderShape
import org.qraft.render.ModuleShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle

@Composable
fun StyleControls(
    style: QrStyle,
    onStyleChange: (QrStyle) -> Unit,
    onPickBackground: () -> Unit,
    onClearBackground: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val quietOptions = listOf(4, 6, 8).filter { it >= Scannability.MIN_QUIET_ZONE_MODULES }
    val eyeOptions = listOf(style.foregroundArgb, 0xFF0D47A1.toInt(), 0xFF1B5E20.toInt(), 0xFFE65100.toInt())
    val contrast = Scannability.contrastRatio(style.foregroundArgb, style.backgroundArgb)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        MenuField(
            label = stringResource(R.string.style_theme),
            value = style.colorTheme,
            options = ColorTheme.entries.toList(),
            labelOf = { it.name },
            onSelect = { onStyleChange(ColorThemes.apply(it, style)) },
        )
        MenuField(
            label = stringResource(R.string.style_modules),
            value = style.moduleShape,
            options = ModuleShape.entries.toList(),
            labelOf = { it.name },
            onSelect = { onStyleChange(style.copy(moduleShape = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_finder),
            value = style.finderShape,
            options = FinderShape.entries.toList(),
            labelOf = { it.name },
            onSelect = { onStyleChange(style.copy(finderShape = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_finder_pupil),
            value = style.finderPupil,
            options = FinderShape.entries.toList(),
            labelOf = { it.name },
            onSelect = { onStyleChange(style.copy(finderPupil = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_eye),
            value = style.eyeColorArgb,
            options = eyeOptions.distinct(),
            labelOf = { "#%06X".format(it and 0xFFFFFF) },
            onSelect = { onStyleChange(style.copy(eyeColorArgb = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_center),
            value = style.centerMark,
            options = CenterMark.entries.toList(),
            labelOf = { it.name },
            onSelect = { mark ->
                onStyleChange(
                    style.copy(
                        centerMark = mark,
                        logoCutout = style.logoCutout.copy(enabled = mark != CenterMark.NONE),
                    ),
                )
            },
        )
        MenuField(
            label = stringResource(R.string.style_quiet_zone, style.quietZoneModules),
            value = style.quietZoneModules,
            options = quietOptions,
            labelOf = { it.toString() },
            onSelect = { onStyleChange(style.copy(quietZoneModules = it)) },
        )
        MenuField(
            label = stringResource(R.string.style_frame),
            value = style.frame,
            options = QrFrame.entries.toList(),
            labelOf = { it.name },
            onSelect = { onStyleChange(style.copy(frame = it)) },
        )
        Text(text = stringResource(R.string.style_corner_badge))
        Switch(
            checked = style.cornerBadge,
            onCheckedChange = { onStyleChange(style.copy(cornerBadge = it)) },
        )
        Text(text = stringResource(R.string.style_gradient))
        Switch(
            checked = style.gradient.enabled,
            onCheckedChange = { on ->
                onStyleChange(style.copy(gradient = style.gradient.copy(enabled = on)))
            },
        )
        Text(text = stringResource(R.string.style_oled))
        Switch(
            checked = style.colorTheme == ColorTheme.OLED,
            onCheckedChange = { on ->
                onStyleChange(ColorThemes.apply(if (on) ColorTheme.OLED else ColorTheme.CLASSIC, style))
            },
        )
        TextButton(onClick = onPickBackground) { Text(stringResource(R.string.style_image_bg)) }
        if (style.imageBackgroundPath.isNotBlank()) {
            TextButton(onClick = onClearBackground) { Text(stringResource(R.string.style_image_bg_clear)) }
        }
        OutlinedTextField(
            value = style.caption.text,
            onValueChange = { onStyleChange(style.copy(caption = CaptionSpec(it))) },
            label = { Text(stringResource(R.string.editor_caption)) },
            singleLine = true,
        )
        Text(text = stringResource(R.string.style_contrast, contrast))
    }
}
