package org.qraft.widget

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
internal fun QrWidgetContent(
    bitmap: Bitmap?,
    talkback: String,
    brightenIntent: Intent,
    caption: String?,
    hidden: Boolean,
    plate: Boolean,
    carousel: Boolean,
) {
    val context = LocalContext.current
    val size: DpSize = LocalSize.current
    val chrome = carousel && WidgetSize.showChrome(size)
    val root = GlanceModifier.fillMaxSize().clickable(actionStartActivity(brightenIntent)).let {
        if (plate) it.background(Color.White) else it
    }
    Box(modifier = root, contentAlignment = Alignment.Center) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (hidden || bitmap == null) {
                Text(text = context.getString(R.string.widget_hidden))
            } else {
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = talkback,
                    contentScale = ContentScale.Fit,
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                )
            }
            // Vector Glance Text stays sharp when the widget is resized (unlike baked bitmap text).
            if (caption != null) {
                Text(
                    text = caption,
                    style = TextStyle(
                        color = ColorProvider(Color.Black),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 2.dp),
                )
            }
            if (chrome) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_chevron_left),
                        contentDescription = context.getString(R.string.widget_prev),
                        modifier = GlanceModifier
                            .size(28.dp)
                            .clickable(actionRunCallback<PrevProfileAction>()),
                    )
                    Spacer(modifier = GlanceModifier.width(16.dp))
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_chevron_right),
                        contentDescription = context.getString(R.string.widget_next),
                        modifier = GlanceModifier
                            .size(28.dp)
                            .clickable(actionRunCallback<NextProfileAction>()),
                    )
                }
            }
        }
    }
}
