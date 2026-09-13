package org.qraft.app.ui.tour

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.qraft.app.R
import org.qraft.app.ui.components.BrandMark

@Composable
fun FirstRunTourDialog(
    onFinished: () -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    val bodies = listOf(
        stringResource(R.string.tour_home),
        stringResource(R.string.tour_gallery),
        stringResource(R.string.tour_widget),
    )
    AlertDialog(
        onDismissRequest = onFinished,
        icon = { BrandMark(size = 48.dp) },
        title = { Text(stringResource(R.string.tour_title)) },
        text = { Text(bodies[step.coerceIn(0, bodies.lastIndex)]) },
        confirmButton = {
            TextButton(
                onClick = {
                    if (step >= bodies.lastIndex) onFinished() else step += 1
                },
            ) {
                Text(
                    if (step >= bodies.lastIndex) {
                        stringResource(R.string.tour_done)
                    } else {
                        stringResource(R.string.tour_next)
                    },
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onFinished) {
                Text(stringResource(R.string.tour_skip))
            }
        },
    )
}
