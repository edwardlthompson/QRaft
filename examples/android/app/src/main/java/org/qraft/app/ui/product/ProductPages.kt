package org.qraft.app.ui.product

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import kotlinx.coroutines.launch
import org.qraft.app.R
import org.qraft.app.editor.DraftHistory
import org.qraft.app.editor.DraftSaver
import org.qraft.app.editor.DraftSnap
import org.qraft.app.editor.DraftStore
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.PayloadKind
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.gallery.BgImage
import org.qraft.app.gallery.GallerySave
import org.qraft.app.share.ExportIntake
import org.qraft.app.share.ExportPngSize
import org.qraft.app.share.QrPoster
import org.qraft.app.share.QrShare
import org.qraft.app.share.ShareIntents
import org.qraft.app.shortcut.ProfileShortcuts
import org.qraft.app.ui.nav.GpRoute
import org.qraft.app.ui.profiles.GalleryChromeState
import org.qraft.app.ui.restorePreviousWallpaper
import org.qraft.app.ui.setWallpaperFromDraft
import org.qraft.app.ui.wall.WallpaperScreen
import org.qraft.app.ui.writeWallpaperPng
import org.qraft.app.wifi.CurrentWifi
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.ProfileHistory
import org.qraft.data.ProfileSnapshot
import org.qraft.data.QrProfile
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer
import org.qraft.wallpaper.WallpaperBinder
import org.qraft.wallpaper.WallpaperSafeZone
import org.qraft.wallpaper.WallpaperTarget

@Composable
fun ProductPages(
    route: GpRoute,
    modifier: Modifier = Modifier,
    galleryChrome: GalleryChromeState,
    onOpenGallery: () -> Unit = {},
    onOpenHome: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { DataStoreProfileRepository(context) }
    var draft by rememberSaveable(stateSaver = DraftSaver) { mutableStateOf(EditorDraft()) }
    var styleJson by rememberSaveable { mutableStateOf(QrStyleJson.encode(QrStyle.DEFAULT)) }
    val style = QrStyleJson.decode(styleJson)
    var saveName by rememberSaveable { mutableStateOf("Website") }
    var margin by rememberSaveable { mutableFloatStateOf(WallpaperSafeZone.DEFAULT_MARGIN_FRACTION.toFloat()) }
    var profiles by remember { mutableStateOf(listOf<QrProfile>()) }
    var history by remember { mutableStateOf(listOf<ProfileSnapshot>()) }
    var past by remember { mutableStateOf(listOf<DraftSnap>()) }
    var future by remember { mutableStateOf(listOf<DraftSnap>()) }
    var pngSize by rememberSaveable { mutableStateOf(ExportPngSize.DEFAULT) }
    val size = remember { WallpaperBinder.displaySizePx(context) }
    val saveDoc = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri != null) writeWallpaperPng(context, uri, draft, style, size, margin)
    }
    val pickBg = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { BgImage.import(context, it) }?.let { path ->
            styleJson = QrStyleJson.encode(style.copy(imageBackgroundPath = path))
        }
    }
    val pickLogo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { BgImage.import(context, it) }?.let { path ->
            styleJson = QrStyleJson.encode(logoStyleFromPath(style, path))
        }
    }
    val pickPoster = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val path = uri?.let { BgImage.import(context, it) } ?: return@rememberLauncherForActivityResult
        val matrix = draft.toPayload()?.encodeText()?.let { QrShare.encodeOrNull(it, style) }
        if (matrix == null) {
            Toast.makeText(context, R.string.editor_payload_empty, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val qr = StyledQrRenderer.render(matrix, 512, style, RasterExtrasFactory.of(style, draft.kind))
        QrPoster.compose(path, qr)?.let { QrShare.shareBitmap(context, it, "qraft-poster.png") }
    }
    val pickExport = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val applied = ExportIntake.apply(ShareIntents.readFromUri(context.contentResolver, uri))
        if (applied == null) {
            if (uri != null) Toast.makeText(context, R.string.import_json_invalid, Toast.LENGTH_LONG).show()
        } else {
            draft = applied.first
            styleJson = QrStyleJson.encode(applied.second)
        }
    }
    val wifiPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, R.string.editor_wifi_unavailable, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val ssid = CurrentWifi.ssidOrNull(context)
        if (ssid == null) {
            Toast.makeText(context, R.string.editor_wifi_unavailable, Toast.LENGTH_SHORT).show()
        } else {
            draft = draft.copy(kind = PayloadKind.Wifi, primary = ssid)
        }
    }
    val activity = context as? ComponentActivity
    var intakeGen by remember { mutableIntStateOf(0) }
    var draftReady by remember { mutableStateOf(false) }
    DisposableEffect(activity) {
        if (activity == null) return@DisposableEffect onDispose {}
        val listener = Consumer<Intent> { incoming ->
            activity.setIntent(incoming)
            intakeGen += 1
        }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
    LaunchedEffect(Unit) {
        repo.seedIfEmpty()
        profiles = repo.all()
        val shared = activity?.intent?.let { intent ->
            ShareIntents.fromIntent(
                intent.action,
                intent.type,
                intent.getStringExtra(Intent.EXTRA_TEXT)
                    ?: intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                    ?: intent.dataString,
                ShareIntents.readFromUri(
                    context.contentResolver,
                    androidx.core.content.IntentCompat.getParcelableExtra(
                        intent,
                        Intent.EXTRA_STREAM,
                        android.net.Uri::class.java,
                    ),
                ),
            )
        }
        if (shared != null) {
            draft = shared
        } else {
            val deepId = when {
                ProfileShortcuts.wantsLastCard(activity?.intent) ->
                    ProfileShortcuts.lastProfileId(context)
                        ?: profiles.maxByOrNull { it.updatedAt }?.id
                else -> ProfileShortcuts.profileIdFromIntent(activity?.intent)
            }
            deepId?.let { repo.get(it) }?.let { loaded ->
                val applied = ProfileApply.fromProfile(loaded)
                draft = applied.first
                styleJson = QrStyleJson.encode(applied.second)
                saveName = loaded.name
            } ?: DraftStore.loadDraft(context)?.let { stored ->
                draft = stored
                val storedStyle = DraftStore.loadStyleJson(context)
                if (storedStyle.isNotBlank()) styleJson = storedStyle
                saveName = DraftStore.loadName(context)
            }
        }
        draftReady = true
    }
    LaunchedEffect(intakeGen) {
        if (intakeGen == 0) return@LaunchedEffect
        ShareIntents.applyIncoming(context, activity?.intent) { next -> draft = next }
    }
    LaunchedEffect(draftReady, draft, styleJson, saveName) {
        if (!draftReady) return@LaunchedEffect
        DraftStore.save(context, draft, styleJson, saveName)
        val next = DraftHistory.push(past, future, DraftSnap(draft, styleJson, saveName))
        past = next.first
        future = next.second
    }
    fun refresh() { scope.launch { profiles = repo.all() } }
    fun clip(text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("qraft", text))
    }
    fun matrixOrNull() = draft.toPayload()?.encodeText()?.let { QrShare.encodeOrNull(it, style) }
    fun extrasOf() = RasterExtrasFactory.of(style, draft.kind)
    fun saveGallery() = scope.launch {
        val profile = GallerySave.write(context, repo, saveName, draft, style, System.currentTimeMillis())
            ?: return@launch
        history = ProfileHistory.push(
            history,
            ProfileSnapshot(profile.payloadText, profile.styleJson, profile.updatedAt),
        )
        ProfileShortcuts.publishOpen(context, profile)
        refresh()
        onOpenGallery()
    }
    val validation = if (draft.primary.isBlank()) context.getString(R.string.editor_payload_empty) else null
    val editor: @Composable (Boolean) -> Unit = { canSave ->
        ProductHomeEditor(
            draft = draft,
            style = style,
            styleJson = styleJson,
            saveName = saveName,
            canSave = canSave && draft.toPayload() != null,
            validation = validation,
            pngSize = pngSize,
            past = past,
            future = future,
            onDraft = { draft = it },
            onStyleJson = { styleJson = it },
            onSaveName = { saveName = it },
            onPastFuture = { p, f -> past = p; future = f },
            onSave = { saveGallery() },
            matrixOrNull = ::matrixOrNull,
            extrasOf = ::extrasOf,
            clip = ::clip,
            pickBg = pickBg,
            pickLogo = pickLogo,
            pickExport = pickExport,
            pickPoster = pickPoster,
            wifiPermission = wifiPermission,
            modifier = modifier,
        )
    }
    when (route) {
        GpRoute.Home, GpRoute.Style -> editor(true)
        GpRoute.Profiles -> ProductProfilesPage(
            profiles = profiles,
            history = history,
            pngSize = pngSize,
            onPngSize = { pngSize = it },
            onHistory = { history = it },
            onDraftStyle = { d, sj, name -> draft = d; styleJson = sj; saveName = name },
            onOpenHome = onOpenHome,
            size = size,
            margin = margin,
            context = context,
            scope = scope,
            repo = repo,
            clip = ::clip,
            refresh = { profiles = repo.all() },
            chrome = galleryChrome,
            modifier = modifier,
        )
        GpRoute.Wallpaper -> WallpaperScreen(
            draft = draft, style = style, marginFraction = margin, onMarginChange = { margin = it },
            widthPx = size.first, heightPx = size.second,
            onSetHome = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.HOME) } },
            onSetLock = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.LOCK) } },
            onSetBoth = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.BOTH) } },
            onSavePng = { saveDoc.launch("qraft-wallpaper.png") },
            onRestore = { scope.launch { restorePreviousWallpaper(context, WallpaperTarget.BOTH) } },
            onPair = {
                scope.launch {
                    setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.BOTH, pairDarkLight = true)
                }
            },
            modifier = modifier,
        )
        else -> editor(false)
    }
}
