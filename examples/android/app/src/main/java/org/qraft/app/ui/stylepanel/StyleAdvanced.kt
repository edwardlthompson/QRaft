package org.qraft.app.ui.stylepanel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.ui.editor.MenuField
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.coreqr.Scannability
import org.qraft.render.ColorTheme
import org.qraft.render.ColorThemes
import org.qraft.render.FinderShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle

@Composable
fun StyleAdvancedControls(
    style: QrStyle,
    onStyleChange: (QrStyle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val quietOptions = listOf(4, 6, 8).filter { it >= Scannability.MIN_QUIET_ZONE_MODULES }
    val scheme = MaterialTheme.colorScheme
    val eyeOptions = listOf(
        style.foregroundArgb,
        scheme.primary.toArgb(),
        scheme.tertiary.toArgb(),
        scheme.error.toArgb(),
    ).distinct()
    val contrast = Scannability.contrastRatio(style.foregroundArgb, style.backgroundArgb)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
        MenuField(
            label = stringResource(R.string.style_theme),
            value = style.colorTheme,
            options = ColorTheme.entries.toList(),
            labelOf = { stringResource(StyleLabels.themeLabelRes(it)) },
            onSelect = { onStyleChange(ColorThemes.apply(it, style)) },
        )
        MenuField(
            label = stringResource(R.string.style_finder),
            value = style.finderShape,
            options = FinderShape.entries.toList(),
            labelOf = { stringResource(StyleLabels.finderLabelRes(it)) },
            onSelect = { onStyleChange(StyleLookFold.withFinder(style, it)) },
        )
        MenuField(
            label = stringResource(R.string.style_eye),
            value = style.eyeColorArgb,
            options = eyeOptions,
            labelOf = { argb ->
                stringResource(
                    when (argb) {
                        style.foregroundArgb -> R.string.style_eye_modules
                        scheme.primary.toArgb() -> R.string.style_eye_primary
                        scheme.tertiary.toArgb() -> R.string.style_eye_tertiary
                        scheme.error.toArgb() -> R.string.style_eye_error
                        else -> R.string.style_eye_custom
                    },
                )
            },
            onSelect = { onStyleChange(style.copy(eyeColorArgb = it)) },
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
            labelOf = { stringResource(StyleLabels.frameLabelRes(it)) },
            onSelect = { onStyleChange(style.copy(frame = it)) },
        )
        StyleToggle(
            title = stringResource(R.string.style_corner_badge),
            checked = style.cornerBadge,
            onCheckedChange = { onStyleChange(style.copy(cornerBadge = it)) },
        )
        StyleToggle(
            title = stringResource(R.string.style_gradient),
            checked = style.gradient.enabled,
            onCheckedChange = { on ->
                onStyleChange(style.copy(gradient = style.gradient.copy(enabled = on)))
            },
        )
        Text(text = stringResource(R.string.style_contrast, contrast))
    }
}
