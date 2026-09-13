package org.qraft.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.qraft.app.R

/** Product mark for TopAppBar, About, and other chrome. */
@Composable
fun BrandMark(modifier: Modifier = Modifier, size: Dp = 32.dp) {
    Image(
        painter = painterResource(R.drawable.ic_brand_mark),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
    )
}
