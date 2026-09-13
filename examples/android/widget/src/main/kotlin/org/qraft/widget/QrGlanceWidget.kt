package org.qraft.widget

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import org.qraft.coreqr.EccPolicy
import org.qraft.coreqr.QrEncoder
import org.qraft.coreqr.QrSurface
import org.qraft.data.DataStoreProfileRepository
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer

class QrGlanceWidget : GlanceAppWidget() {
    /** Exact tracks free (non-square) resize; one small bitmap stays binder-safe. */
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = DataStoreProfileRepository(context)
        repo.seedIfEmpty()
        val profiles = repo.all()
        val manager = GlanceAppWidgetManager(context)
        val widgetId = runCatching { manager.getAppWidgetId(id) }.getOrDefault(0)
        WidgetPrefs.consumePendingIfNeeded(context, widgetId)
        val profile = repo.get(WidgetPrefs.selectedId(context, widgetId))
            ?: profiles.firstOrNull()
            ?: DataStoreProfileRepository.seedWebsite()
        val hide = profile.sensitive && WidgetPrefs.sensitiveLock(context, widgetId)
        val transparent = WidgetPrefs.transparentBg(context, widgetId)
        val style = WidgetPaint.style(QrStyleJson.decode(profile.styleJson), transparent)
        val payload = profile.payloadText.ifBlank { "https://example.com" }
        val talkback = context.getString(
            R.string.widget_talkback,
            WidgetTalkback.talkbackName(profile.name, payload),
        )
        val density = context.resources.displayMetrics.density
        val sizes = runCatching { manager.getAppWidgetSizes(id) }.getOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(DpSize(110.dp, 110.dp))
        val targetPx = sizes.maxOf { WidgetSize.exactPx(density, it) }
        // Style caption always; checkbox also allows the card name as a readable label.
        // Caption is drawn as Glance Text (vector), not scaled bitmap pixels.
        val captionText = WidgetCaption.forBitmap(
            profile,
            nameFallback = WidgetPrefs.captionEnabled(context, widgetId),
        ).ifBlank { null }
        val bitmap = if (hide) null else renderBitmap(
            profile.id,
            profile.styleJson,
            payload,
            style,
            targetPx,
        )
        val brighten = BrightenIntents.activityIntent(
            context,
            profile,
            WidgetPrefs.sensitiveLock(context, widgetId),
        )
        provideContent {
            QrWidgetContent(
                bitmap = bitmap,
                talkback = talkback,
                brightenIntent = brighten,
                caption = captionText,
                hidden = hide,
                plate = !transparent,
                carousel = WidgetPrefs.carouselEnabled(context, widgetId),
            )
        }
    }

    private fun renderBitmap(
        profileId: String,
        styleJson: String,
        payload: String,
        style: QrStyle,
        targetPx: Int,
    ): Bitmap {
        val matrix = QrEncoder.encodeText(
            payload,
            errorCorrection = EccPolicy.choose(QrSurface.WIDGET, style.hasOverlay),
            boostEcl = EccPolicy.boostEcl(QrSurface.WIDGET, style.hasOverlay),
        )
        val quiet = style.quietZoneModules.coerceAtLeast(0)
        val px = WidgetQrCanvas.snapToModules(targetPx, matrix.size + 2 * quiet)
        val key = CACHE.key(profileId, "$styleJson#bg=${style.backgroundArgb}", px)
        CACHE.get(key)?.let { hit ->
            return Bitmap.createBitmap(hit.pixels, hit.width, hit.height, Bitmap.Config.ARGB_8888)
        }
        val bmp = StyledQrRenderer.render(matrix, px, style, applyCaption = false)
        val pixels = IntArray(bmp.width * bmp.height)
        bmp.getPixels(pixels, 0, bmp.width, 0, 0, bmp.width, bmp.height)
        CACHE.put(key, bmp.width, bmp.height, pixels)
        return bmp
    }

    companion object {
        internal val CACHE: QrWidgetCache = WidgetRefresh.sharedCache()
    }
}

class NextProfileAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters,
    ) {
        cycleProfiles(context, glanceId, delta = 1)
    }
}

class PrevProfileAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters,
    ) {
        cycleProfiles(context, glanceId, delta = -1)
    }
}

private suspend fun cycleProfiles(context: Context, glanceId: GlanceId, delta: Int) {
    val repo = DataStoreProfileRepository(context)
    val wid = runCatching {
        GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
    }.getOrDefault(0)
    if (!WidgetPrefs.carouselEnabled(context, wid)) return
    WidgetPrefs.cycle(context, repo.all().map { it.id }, wid, delta)
    QrGlanceWidget().update(context, glanceId)
}

class QrGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QrGlanceWidget()
}
