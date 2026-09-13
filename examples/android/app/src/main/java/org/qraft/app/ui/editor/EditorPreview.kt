package org.qraft.app.ui.editor

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.coreqr.EccPolicy
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrMatrix
import org.qraft.coreqr.QrSurface
import org.qraft.coreqr.Scannability
import org.qraft.render.CaptionSpec
import org.qraft.render.QrStyle
import org.qraft.render.StyledQrRenderer
import org.qraft.scan.Barcode1dKind
import org.qraft.scan.BarcodeEncoder
import kotlin.math.max

@Composable
fun EditorPreview(
    draft: EditorDraft,
    style: QrStyle,
    surface: QrSurface = QrSurface.EDITOR,
) {
    val fontScale = LocalDensity.current.fontScale
    val previewDp = (240f * max(1f, fontScale)).dp
    val overlay = style.hasOverlay
    val ecc = EccPolicy.choose(surface, overlay)
    val payload = draft.toPayload()
    val extras = remember(style.centerMark, draft.kind, style.imageBackgroundPath, style.logoImagePath) {
        RasterExtrasFactory.of(style, draft.kind)
    }
    val barcodeBmp: Bitmap? = remember(draft.kind, draft.primary, draft.secondary, style.foregroundArgb, style.backgroundArgb) {
        if (draft.kind != PayloadKind.Barcode) return@remember null
        val kind = when (draft.secondary) {
            "Ean13" -> Barcode1dKind.Ean13
            "Code39" -> Barcode1dKind.Code39
            else -> Barcode1dKind.Code128
        }
        val matrix = BarcodeEncoder.encode(kind, draft.primary) ?: return@remember null
        val pixels = BarcodeEncoder.toArgb(matrix, style.foregroundArgb, style.backgroundArgb)
        Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
    }
    // Strip caption so typing under the QR does not re-rasterize modules every keystroke.
    val rasterStyle = style.copy(caption = CaptionSpec())
    val matrix: QrMatrix? = remember(payload, ecc, overlay, surface, draft.kind) {
        if (draft.kind == PayloadKind.Barcode) return@remember null
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
    val baseBitmap: Bitmap? = remember(matrix, rasterStyle, extras, barcodeBmp) {
        barcodeBmp ?: matrix?.let {
            StyledQrRenderer.render(it, sizePx = 512, style = rasterStyle, extras = extras, applyCaption = false)
        }
    }
    var captionText by remember { mutableStateOf(style.caption.text) }
    LaunchedEffect(style.caption.text) {
        delay(120)
        captionText = style.caption.text
    }
    val bitmap: Bitmap? = remember(baseBitmap, captionText, style.foregroundArgb, style.backgroundArgb) {
        val base = baseBitmap ?: return@remember null
        if (captionText.isBlank()) return@remember base
        val captionStyle = style.copy(caption = CaptionSpec(captionText))
        // Copy so withCaption can recycle the temporary; never recycle baseBitmap (Compose may still draw it).
        StyledQrRenderer.withCaption(
            base.copy(Bitmap.Config.ARGB_8888, false),
            captionStyle,
            recycleSource = true,
        )
    }
    val warnings = remember(matrix, rasterStyle, overlay) {
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
            val aspect = bitmap.height.toFloat() / bitmap.width.toFloat()
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.home_preview_cd, payload?.encodeText().orEmpty()),
                modifier = Modifier.size(
                    width = previewDp,
                    height = previewDp * aspect,
                ),
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
