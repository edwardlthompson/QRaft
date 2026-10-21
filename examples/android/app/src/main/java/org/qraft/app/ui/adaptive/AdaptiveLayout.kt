package org.qraft.app.ui.adaptive

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.ui.theme.SpacingMd

/** App-facing width buckets mapped from Material WindowWidthSizeClass. */
enum class QraftWidthClass {
    Compact,
    Medium,
    Expanded,
    ;

    val isTwoPane: Boolean get() = this != Compact

    companion object {
        /**
         * Maps Material width size class to [QraftWidthClass].
         * Unknown / future values fall back to Compact (stacked phone layout).
         */
        fun from(widthSizeClass: WindowWidthSizeClass): QraftWidthClass = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> Compact
            WindowWidthSizeClass.Medium -> Medium
            WindowWidthSizeClass.Expanded -> Expanded
            else -> Compact
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberQraftWidthClass(): QraftWidthClass {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    if (activity == null) {
        // Previews / non-Activity hosts: keep compact stacked layout.
        return QraftWidthClass.Compact
    }
    val windowSizeClass = calculateWindowSizeClass(activity)
    return QraftWidthClass.from(windowSizeClass.widthSizeClass)
}

/**
 * Compact: stacked [preview] then [content].
 * Medium/Expanded: sticky [preview] beside scrollable [content].
 */
@Composable
fun AdaptiveTwoPane(
    widthClass: QraftWidthClass,
    preview: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (widthClass.isTwoPane) {
        Row(
            modifier = modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter,
            ) {
                preview()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .highRefreshScroll()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                content()
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            preview()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .highRefreshScroll()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                content()
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
