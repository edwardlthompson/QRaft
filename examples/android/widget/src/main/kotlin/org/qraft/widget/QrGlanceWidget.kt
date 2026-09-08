package org.qraft.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.Box
import androidx.glance.text.Text

/**
 * Glance QR widget scaffold. Bitmap caching, carousel, blur/lock, and tap-to-brighten
 * land in later sprints; this registers a resizable home/lock-panel-compatible receiver.
 */
class QrGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            QrWidgetContent()
        }
    }
}

@Composable
private fun QrWidgetContent() {
    Box(
        modifier = GlanceModifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "QRaft")
    }
}

class QrGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QrGlanceWidget()
}
