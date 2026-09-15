package org.qraft.app.ui.tour

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import org.qraft.app.R
import org.qraft.app.display.reduceMotion
import org.qraft.app.ui.components.BrandMark
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.SpacingMd

@Composable
fun FirstRunTourDialog(
    onFinished: () -> Unit,
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    val bodies = TourSteps.BODY_RES.map { stringResource(it) }
    val index = step.coerceIn(0, bodies.lastIndex)
    val motionOff = reduceMotion()
    AlertDialog(
        onDismissRequest = onFinished,
        icon = { BrandMark(size = MinTouchDp) },
        title = { Text(stringResource(R.string.tour_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(SpacingMd)) {
                Text(
                    text = stringResource(R.string.tour_step_of, index + 1, bodies.size),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
                if (motionOff) {
                    Text(bodies[index])
                } else {
                    Crossfade(
                        targetState = index,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        label = "tourBody",
                    ) { i -> Text(bodies[i]) }
                }
            }
        },
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
