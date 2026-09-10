package org.qraft.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import org.qraft.coreqr.QrEncoder
import org.qraft.data.DataStoreProfileRepository
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRasterizer
import org.qraft.render.StyledQrRenderer

class QrGlanceWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Responsive(
        setOf(DpSize(40.dp, 40.dp), DpSize(110.dp, 110.dp), DpSize(180.dp, 180.dp)),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = DataStoreProfileRepository(context)
        repo.seedIfEmpty()
        val profiles = repo.all()
        val widgetId = runCatching {
            androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(id)
        }.getOrDefault(0)
        val profile = repo.get(WidgetPrefs.selectedId(context, widgetId))
            ?: profiles.firstOrNull()
            ?: DataStoreProfileRepository.seedWebsite()
        val hide = profile.sensitive && WidgetPrefs.sensitiveLock(context)
        val style = WidgetPaint.style(QrStyleJson.decode(profile.styleJson), WidgetPrefs.transparentBg(context))
        val payload = profile.payloadText.ifBlank { "https://example.com" }
        val talkback = context.getString(R.string.widget_talkback, WidgetTalkback.label(profile.name))
        val uri = if (hide) null else cachedOrRender(context, profile.id, profile.styleJson, payload, style)
        provideContent {
            QrWidgetContent(
                imageUri = uri,
                talkback = talkback,
                payload = payload,
                styleJson = profile.styleJson,
                caption = if (WidgetPrefs.captionEnabled(context)) profile.name else null,
                hidden = hide,
            )
        }
    }

    private fun cachedOrRender(
        context: Context,
        profileId: String,
        styleJson: String,
        payload: String,
        style: org.qraft.render.QrStyle,
    ): android.net.Uri {
        val key = CACHE.key(profileId, styleJson, WidgetSize.RENDER_PX)
        val bmp = CACHE.get(key)?.let { hit ->
            Bitmap.createBitmap(hit.pixels, hit.width, hit.height, Bitmap.Config.ARGB_8888)
        } ?: run {
            val matrix = QrEncoder.encodeText(
                payload,
                errorCorrection = org.qraft.coreqr.EccPolicy.choose(
                    org.qraft.coreqr.QrSurface.WIDGET,
                    style.hasOverlay,
                ),
                boostEcl = org.qraft.coreqr.EccPolicy.boostEcl(
                    org.qraft.coreqr.QrSurface.WIDGET,
                    style.hasOverlay,
                ),
            )
            val raster = StyledQrRasterizer.rasterize(matrix, WidgetSize.RENDER_PX, style)
            CACHE.put(key, raster.width, raster.height, raster.pixels)
            StyledQrRenderer.render(matrix, WidgetSize.RENDER_PX, style, applyCaption = false)
        }
        val file = java.io.File(context.cacheDir, "glance-$profileId.png")
        file.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return android.net.Uri.fromFile(file)
    }

    companion object {
        private val CACHE = QrWidgetCache()
    }
}

@Composable
private fun QrWidgetContent(
    imageUri: android.net.Uri?,
    talkback: String,
    payload: String,
    styleJson: String,
    caption: String?,
    hidden: Boolean,
) {
    val context = LocalContext.current
    val intent = Intent(context, BrightenActivity::class.java).apply {
        putExtra(BrightenActivity.EXTRA_PAYLOAD, payload)
        putExtra(BrightenActivity.EXTRA_STYLE, styleJson)
    }
    Column(
        modifier = GlanceModifier.fillMaxSize().clickable(actionStartActivity(intent)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = GlanceModifier.defaultWeight(), contentAlignment = Alignment.Center) {
            if (hidden || imageUri == null) {
                Text(text = context.getString(R.string.widget_hidden))
            } else {
                Image(provider = ImageProvider(imageUri), contentDescription = talkback)
            }
        }
        if (caption != null) Text(text = caption)
        Text(
            text = context.getString(R.string.widget_next),
            modifier = GlanceModifier.clickable(actionRunCallback<NextProfileAction>()),
        )
    }
}

class NextProfileAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: androidx.glance.action.ActionParameters) {
        val repo = DataStoreProfileRepository(context)
        WidgetPrefs.cycle(context, repo.all().map { it.id }, runCatching {
            androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
        }.getOrDefault(0))
        QrGlanceWidget().update(context, glanceId)
    }
}

class QrGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QrGlanceWidget()
}
