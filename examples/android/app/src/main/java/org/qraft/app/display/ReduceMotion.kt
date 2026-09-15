package org.qraft.app.display

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

object ReduceMotion {
    fun enabled(animatorDurationScale: Float): Boolean = animatorDurationScale == 0f

    fun allowHaptic(animatorDurationScale: Float): Boolean = !enabled(animatorDurationScale)

    fun durationMs(animatorDurationScale: Float, ms: Int): Int =
        if (enabled(animatorDurationScale)) 0 else ms
}

@Composable
fun reduceMotion(): Boolean {
    val context = LocalContext.current
    return remember {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
        ReduceMotion.enabled(scale)
    }
}
