package org.qraft.app.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.display.highRefreshScroll
import org.qraft.app.display.reduceMotion
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.EditorStep
import org.qraft.app.editor.EditorSteps
import org.qraft.app.share.ExportPngSize
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.SpacingMd
import org.qraft.app.ui.theme.VisualHierarchy
import org.qraft.render.QrStyle

@Composable
fun EditorScreen(
    draft: EditorDraft,
    style: QrStyle,
    onDraftChange: (EditorDraft) -> Unit,
    onStyleChange: (QrStyle) -> Unit,
    saveName: String,
    onSaveNameChange: (String) -> Unit,
    canSave: Boolean,
    validation: String?,
    onSave: () -> Unit,
    onExportJson: () -> Unit,
    onExportPng: () -> Unit,
    onExportSvg: () -> Unit,
    onExportPdf: () -> Unit,
    onStyleQr: () -> Unit,
    onPickBackground: () -> Unit,
    onClearBackground: () -> Unit,
    onPickLogo: () -> Unit = {},
    onClearLogo: () -> Unit = {},
    onAddWidget: () -> Unit,
    onImportJson: () -> Unit = {},
    onPrint: () -> Unit = {},
    onZip: () -> Unit = {},
    onCurrentWifi: () -> Unit,
    onPasteClipboard: () -> Unit = {},
    onDecoratePhoto: () -> Unit = {},
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {},
    onDuplicate: () -> Unit = {},
    onNew: () -> Unit = {},
    canUndo: Boolean = false,
    canRedo: Boolean = false,
    step: EditorStep,
    onStep: (EditorStep) -> Unit,
    pngSize: ExportPngSize = ExportPngSize.DEFAULT,
    onPngSize: (ExportPngSize) -> Unit = {},
    onWallpaperHome: () -> Unit = {},
    onWallpaperLock: () -> Unit = {},
    onWallpaperBoth: () -> Unit = {},
    onWallpaperRestore: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var enlarged by rememberSaveable { mutableStateOf(false) }
    var moreOpen by rememberSaveable { mutableStateOf(false) }
    var moreCollapsed by rememberSaveable { mutableStateOf(false) }
    var advancedOpen by rememberSaveable { mutableStateOf(false) }
    var advancedCollapsed by rememberSaveable { mutableStateOf(false) }
    var confirmStyleQr by rememberSaveable { mutableStateOf(false) }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(draft.kind) {
        if (EditorSteps.showMoreTypes(draft.kind) && !moreCollapsed) moreOpen = true
        if (!EditorSteps.showMoreTypes(draft.kind)) moreCollapsed = false
    }
    LaunchedEffect(EditorSteps.isAdvanced(style)) {
        if (EditorSteps.isAdvanced(style) && !advancedCollapsed) advancedOpen = true
    }
    BackHandler(enabled = EditorSteps.consumeBack(step) != null) {
        EditorSteps.consumeBack(step)?.let(onStep)
    }
    LaunchedEffect(step) {
        if (step == EditorStep.Share) onStep(EditorStep.Place)
    }
    if (confirmStyleQr) {
        AlertDialog(
            onDismissRequest = { confirmStyleQr = false },
            text = { Text(stringResource(R.string.export_style_qr_confirm)) },
            confirmButton = {
                TextButton(onClick = { confirmStyleQr = false; onStyleQr() }) {
                    Text(stringResource(R.string.export_style_qr))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmStyleQr = false }) {
                    Text(stringResource(R.string.editor_dialog_cancel))
                }
            },
        )
    }
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val cap = maxHeight.value * 0.4f
        val motionOff = reduceMotion()
        val previewTarget = if (imeVisible || !enlarged) {
            EditorSteps.PREVIEW_COMPACT_DP
        } else {
            EditorSteps.PREVIEW_FULL_DP
        }
        val previewDp by animateFloatAsState(
            targetValue = previewTarget,
            animationSpec = tween(
                durationMillis = if (motionOff) 0 else 200,
                easing = FastOutSlowInEasing,
            ),
            label = "previewSize",
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(SpacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Surface(tonalElevation = VisualHierarchy.PreviewElevation, modifier = Modifier.fillMaxWidth()) {
                EditorPreview(
                    draft = draft,
                    style = style,
                    sizeDp = previewDp,
                    maxSizeDp = cap,
                    showEcc = EditorSteps.showEcc(step, style.hasOverlay),
                    modifier = Modifier
                        .clickable(
                            onClickLabel = stringResource(R.string.editor_preview_toggle),
                        ) { if (!imeVisible) enlarged = !enlarged }
                        .padding(SpacingMd),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(SpacingMd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (EditorSteps.SHOW_CHIPS) {
                    val shown = EditorSteps.shown(step)
                    EditorSteps.CHIPS.forEach { item ->
                        FilterChip(
                            selected = item == shown,
                            onClick = { onStep(item) },
                            label = { Text(stringResource(stepLabel(item))) },
                            modifier = Modifier.widthIn(min = MinTouchDp),
                        )
                    }
                }
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.widthIn(min = MinTouchDp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(R.string.editor_undo))
                }
                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.widthIn(min = MinTouchDp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = stringResource(R.string.editor_redo))
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding()
                    .highRefreshScroll()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                val pane: @Composable (EditorStep) -> Unit = { current ->
                    when (current) {
                        EditorStep.Content -> EditorStepContent(
                            draft = draft,
                            onDraftChange = onDraftChange,
                            validation = validation,
                            onCurrentWifi = onCurrentWifi,
                            onPasteClipboard = onPasteClipboard,
                            moreOpen = moreOpen,
                            onToggleMore = {
                                moreOpen = !moreOpen
                                moreCollapsed = EditorSteps.showMoreTypes(draft.kind) && !moreOpen
                            },
                        )
                        EditorStep.Look -> EditorStepLook(
                            style = style,
                            onStyleChange = onStyleChange,
                            onPickBackground = onPickBackground,
                            onClearBackground = onClearBackground,
                            onPickLogo = onPickLogo,
                            onClearLogo = onClearLogo,
                            onDecoratePhoto = onDecoratePhoto,
                            advancedOpen = advancedOpen,
                            onToggleAdvanced = {
                                advancedOpen = !advancedOpen
                                advancedCollapsed = EditorSteps.isAdvanced(style) && !advancedOpen
                            },
                        )
                        EditorStep.Place, EditorStep.Share -> EditorStepPlace(
                            saveName = saveName,
                            onSaveNameChange = onSaveNameChange,
                            canSetWallpaper = draft.toPayload() != null,
                            pngSize = pngSize,
                            onPngSize = onPngSize,
                            onWallpaperHome = onWallpaperHome,
                            onWallpaperLock = onWallpaperLock,
                            onWallpaperBoth = onWallpaperBoth,
                            onWallpaperRestore = onWallpaperRestore,
                            onSharePng = onExportPng,
                        )
                    }
                }
                val shown = EditorSteps.shown(step)
                if (motionOff) {
                    pane(shown)
                } else {
                    Crossfade(
                        targetState = shown,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        label = "editorStep",
                    ) { current -> pane(current) }
                }
            }
            EditorStepFooter(
                step = step,
                canSave = canSave,
                onStep = onStep,
                onSave = onSave,
                onExportPng = onExportPng,
                onExportJson = onExportJson,
                onExportSvg = onExportSvg,
                onExportPdf = onExportPdf,
                onStyleQr = onStyleQr,
                onAddWidget = onAddWidget,
                onImportJson = onImportJson,
                onPrint = onPrint,
                onZip = onZip,
                onConfirmStyleQr = { confirmStyleQr = true },
                onDuplicate = onDuplicate,
                onNew = onNew,
            )
        }
    }
}
