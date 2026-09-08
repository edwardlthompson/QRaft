package org.qraft.app.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import org.qraft.app.ui.theme.SpacingLg
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.coreqr.ErrorCorrectionLevel
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrPayload
import org.qraft.render.SquareQrRenderer

@Composable
fun HomeQrPreview(modifier: Modifier = Modifier) {
    val previewUrl = stringResource(R.string.home_preview_url)
    val bitmap: Bitmap = remember(previewUrl) {
        val matrix = QrEncoder.encode(
            QrPayload.Url(previewUrl),
            errorCorrection = ErrorCorrectionLevel.M,
        )
        SquareQrRenderer.render(matrix, sizePx = 512)
    }
    Column(
        modifier = modifier.padding(SpacingMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.app_greeting),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.home_preview_caption),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = SpacingMd, bottom = SpacingLg),
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.home_preview_cd),
            modifier = Modifier.size(240.dp),
        )
        Text(
            text = stringResource(R.string.app_status_offline),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = SpacingLg),
        )
    }
}
