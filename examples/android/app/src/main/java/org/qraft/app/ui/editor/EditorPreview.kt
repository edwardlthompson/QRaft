package org.qraft.app.ui.editor

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.coreqr.EccPolicy
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrMatrix
import org.qraft.coreqr.QrSurface
import org.qraft.coreqr.Scannability
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRenderer

@Composable
fun EditorPreview(
    draft: EditorDraft,
    style: QrStyle,
    surface: QrSurface = QrSurface.EDITOR,
) {
    val overlay = style.hasOverlay
    val ecc = EccPolicy.choose(surface, overlay)
    val payload = draft.toPayload()
    val extras = remember(style.centerMark, draft.kind, style.imageBackgroundPath) {
        RasterExtrasFactory.of(style, draft.kind)
    }
    val matrix: QrMatrix? = remember(payload, ecc, overlay, surface) {
        payload?.let {
            runCatching {
                QrEncoder.encode(
                    it,
                    errorCorrection = ecc,
                    forceHighEcc = EccPolicy.forceHighEcc(overlay),
                    boostEcl = EccPolicy.boostEcl(surface, overlay),
                )
            }.getOrNull()
        }
    }
    val bitmap: Bitmap? = remember(matrix, style, extras) {
        matrix?.let {
            StyledQrRenderer.render(it, sizePx = 512, style = style, extras = extras, applyCaption = true)
        }
    }
    val warnings = remember(matrix, style, overlay) {
        matrix?.let {
            Scannability.analyze(
                it,
                foregroundArgb = style.foregroundArgb,
                backgroundArgb = style.backgroundArgb,
                quietZoneModules = style.quietZoneModules,
                logoCoverage = if (overlay) style.logoCutout.clampedFraction else 0.0,
                hasOverlay = overlay,
            ).warnings
        }.orEmpty()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_preview_cd, payload?.encodeText().orEmpty()),
                modifier = Modifier.size(width = 240.dp, height = if (style.caption.text.isBlank()) 240.dp else 280.dp),
            )
        } else {
            Text(text = stringResource(R.string.editor_preview_empty), style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = if (overlay) {
                stringResource(R.string.editor_ecc_logo)
            } else {
                stringResource(R.string.editor_ecc_auto, ecc.name)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        warnings.forEach { warning ->
            Text(
                text = stringResource(warningLabel(warning)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

private fun warningLabel(warning: Scannability.Warning): Int = when (warning) {
    Scannability.Warning.LOW_CONTRAST -> R.string.editor_warn_contrast
    Scannability.Warning.LOGO_TOO_LARGE -> R.string.editor_warn_logo
    Scannability.Warning.QUIET_ZONE_TOO_SMALL -> R.string.editor_warn_quiet
    Scannability.Warning.OVERLAY_NEEDS_HIGH_ECC -> R.string.editor_warn_ecc
}
