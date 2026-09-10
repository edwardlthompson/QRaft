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
import org.qraft.app.editor.DraftSaver
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.DraftHistory
import org.qraft.app.editor.DraftSnap
import org.qraft.app.editor.DraftStore
import org.qraft.app.editor.PayloadKind
import org.qraft.app.editor.ProfileApply
import org.qraft.app.editor.RasterExtrasFactory
import org.qraft.app.gallery.BgImage
import org.qraft.app.gallery.GallerySave
import org.qraft.app.gallery.GalleryStore
import org.qraft.app.share.ExportIntake
import org.qraft.app.share.QrShare
import org.qraft.app.share.ShareIntents
import org.qraft.app.share.WidgetPin
import org.qraft.app.shortcut.ProfileShortcuts
import org.qraft.app.ui.editor.EditorScreen
import org.qraft.app.ui.nav.GpRoute
import org.qraft.app.ui.profiles.ProfilesScreen
import org.qraft.app.ui.setWallpaperFromDraft
import org.qraft.app.ui.wall.WallpaperScreen
import org.qraft.app.ui.writeWallpaperPng
import org.qraft.app.wifi.CurrentWifi
import org.qraft.data.BackupCrypto
import org.qraft.data.DataStoreProfileRepository
import org.qraft.data.ProfileHistory
import org.qraft.data.ProfileSnapshot
import org.qraft.data.QrProfile
import org.qraft.render.QrExportDocument
import org.qraft.render.QrExportJson
import org.qraft.render.QrStyle
import org.qraft.render.QrStyleJson
import org.qraft.render.StyleJsonQr
import org.qraft.wallpaper.WallpaperBinder
import org.qraft.wallpaper.WallpaperSafeZone
import org.qraft.wallpaper.WallpaperTarget

@Composable
fun ProductPages(route: GpRoute, modifier: Modifier = Modifier) {
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
    val size = remember { WallpaperBinder.displaySizePx(context) }
    val saveDoc = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        if (uri != null) writeWallpaperPng(context, uri, draft, style, size, margin)
    }
    val pickBg = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { BgImage.import(context, it) }?.let { path ->
            styleJson = QrStyleJson.encode(style.copy(imageBackgroundPath = path))
        }
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
        var shared = false
        ShareIntents.applyIncoming(context, activity?.intent) { next ->
            draft = next
            shared = true
        }
        if (!shared) {
            val extra = activity?.intent?.getStringExtra(ProfileShortcuts.EXTRA_PROFILE_ID)
            extra?.let { repo.get(it) }?.let { loaded ->
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
        val snap = DraftSnap(draft, styleJson, saveName)
        val next = DraftHistory.push(past, future, snap)
        past = next.first
        future = next.second
    }
    fun refresh() { scope.launch { profiles = repo.all() } }
    fun clip(text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("qraft", text))
    }
    fun matrixOrNull() = draft.toPayload()?.encodeText()?.let { QrShare.encodeOrNull(it, style) }
    fun extrasOf(s: QrStyle = style, kind: PayloadKind = draft.kind) = RasterExtrasFactory.of(s, kind)
    fun matrixOf(profile: QrProfile) = QrShare.encodeOrNull(profile.payloadText, QrStyleJson.decode(profile.styleJson))
    fun saveGallery() {
        scope.launch {
            val profile = GallerySave.write(context, repo, saveName, draft, style, System.currentTimeMillis()) ?: return@launch
            history = ProfileHistory.push(history, ProfileSnapshot(profile.payloadText, profile.styleJson, profile.updatedAt))
            ProfileShortcuts.publishOpen(context, profile)
            refresh()
        }
    }
    val validation = if (draft.primary.isBlank()) context.getString(R.string.editor_payload_empty) else null
    val editor: @Composable (Boolean) -> Unit = { canSave ->
        EditorScreen(
            draft = draft, style = style, onDraftChange = { draft = it },
            onStyleChange = { styleJson = QrStyleJson.encode(it) },
            saveName = saveName, onSaveNameChange = { saveName = it },
            canSave = canSave && draft.toPayload() != null, validation = validation,
            onSave = { saveGallery() },
            onExportJson = {
                draft.toPayload()?.encodeText()?.let {
                    clip(QrExportJson.encode(QrExportDocument(payloadText = it, style = style)))
                }
            },
            onExportPng = { matrixOrNull()?.let { QrShare.sharePng(context, it, style, extrasOf()) } },
            onExportSvg = { matrixOrNull()?.let { clip(QrShare.svgText(it, style)) } },
            onExportPdf = { matrixOrNull()?.let { QrShare.sharePdf(context, it, style) } },
            onStyleQr = { draft = EditorDraft(primary = StyleJsonQr.payload(style)) },
            onPickBackground = { pickBg.launch(arrayOf("image/*")) },
            onClearBackground = { styleJson = QrStyleJson.encode(style.copy(imageBackgroundPath = "")) },
            onAddWidget = {
                val id = profiles.firstOrNull { it.payloadText == draft.toPayload()?.encodeText() }?.id
                if (!WidgetPin.request(context, id ?: DataStoreProfileRepository.SEED_ID)) {
                    Toast.makeText(context, R.string.widget_pin_refused, Toast.LENGTH_LONG).show()
                }
            },
            onImportJson = { pickExport.launch(arrayOf("application/json", "text/*", "*/*")) },
            onCurrentWifi = {
                CurrentWifi.ssidOrNull(context)?.let { ssid -> draft = draft.copy(kind = PayloadKind.Wifi, primary = ssid) }
            },
            onUndo = {
                val (snap, p, f) = DraftHistory.undo(past, future)
                past = p
                future = f
                snap?.let {
                    draft = it.draft
                    styleJson = it.styleJson
                    saveName = it.name
                }
            },
            onRedo = {
                val (snap, p, f) = DraftHistory.redo(past, future)
                past = p
                future = f
                snap?.let {
                    draft = it.draft
                    styleJson = it.styleJson
                    saveName = it.name
                }
            },
            onDuplicate = {
                saveName = "$saveName copy"
                saveGallery()
            },
            onNew = {
                draft = EditorDraft()
                styleJson = QrStyleJson.encode(QrStyle.DEFAULT)
                saveName = "Website"
            },
            canUndo = past.size > 1,
            canRedo = future.isNotEmpty(),
            modifier = modifier,
        )
    }
    when (route) {
        GpRoute.Home, GpRoute.Style -> editor(true)
        GpRoute.Profiles -> ProfilesScreen(
            profiles = profiles,
            onLoad = { profile ->
                val applied = ProfileApply.fromProfile(profile)
                draft = applied.first
                styleJson = QrStyleJson.encode(applied.second)
                saveName = profile.name
            },
            onDelete = { id ->
                scope.launch {
                    repo.delete(id)
                    GalleryStore.delete(context, id)
                    refresh()
                }
            },
            onBackup = { secret -> scope.launch { clip(BackupCrypto.wrap(repo.exportJson(), secret)) } },
            onRestore = { secret ->
                val text = (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    .primaryClip?.getItemAt(0)?.coerceToText(context)?.toString().orEmpty()
                val json = BackupCrypto.unwrap(text, secret)
                if (json != null) scope.launch { repo.importJson(json); refresh() }
            },
            onUndo = {
                val (snap, next) = ProfileHistory.undo(history)
                history = next
                if (snap != null) {
                    draft = ProfileApply.fromProfile(QrProfile("undo", "Undo", snap.payloadText, styleJson = snap.styleJson)).first
                    styleJson = snap.styleJson
                }
            },
            onExportJson = { p ->
                clip(QrExportJson.encode(QrExportDocument(payloadText = p.payloadText, style = QrStyleJson.decode(p.styleJson))))
            },
            onExportPng = { p ->
                val s = QrStyleJson.decode(p.styleJson)
                val kind = ProfileApply.fromProfile(p).first.kind
                matrixOf(p)?.let { QrShare.sharePng(context, it, s, extrasOf(s, kind)) }
            },
            onExportSvg = { p -> matrixOf(p)?.let { clip(QrShare.svgText(it, QrStyleJson.decode(p.styleJson))) } },
            onExportPdf = { p -> matrixOf(p)?.let { QrShare.sharePdf(context, it, QrStyleJson.decode(p.styleJson)) } },
            onAddWidget = { p ->
                if (!WidgetPin.request(context, p.id)) {
                    Toast.makeText(context, R.string.widget_pin_refused, Toast.LENGTH_LONG).show()
                }
            },
            onUpdate = { p ->
                scope.launch {
                    repo.upsert(p.copy(updatedAt = System.currentTimeMillis()))
                    refresh()
                }
            },
            modifier = modifier,
        )
        GpRoute.Wallpaper -> WallpaperScreen(
            draft = draft, style = style, marginFraction = margin, onMarginChange = { margin = it },
            widthPx = size.first, heightPx = size.second,
            onSetHome = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.HOME) } },
            onSetLock = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.LOCK) } },
            onSetBoth = { scope.launch { setWallpaperFromDraft(context, draft, style, size, margin, WallpaperTarget.BOTH) } },
            onSavePng = { saveDoc.launch("qraft-wallpaper.png") },
            modifier = modifier,
        )
        else -> editor(false)
    }
}
