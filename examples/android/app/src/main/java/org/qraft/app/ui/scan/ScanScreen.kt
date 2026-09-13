package org.qraft.app.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.qraft.app.R
import org.qraft.app.gallery.GalleryScanClone
import org.qraft.app.scan.ScanHistoryStore
import org.qraft.data.DataStoreProfileRepository
import org.qraft.scan.QrDecoder
import org.qraft.scan.ScanActionKind
import org.qraft.scan.ScanActions

@Composable
fun ScanScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val repo = remember { DataStoreProfileRepository(context) }
    var payload by remember { mutableStateOf<String?>(null) }
    var format by remember { mutableStateOf("QR_CODE") }
    var status by remember { mutableStateOf(context.getString(R.string.scan_hint_point)) }
    var saving by remember { mutableStateOf(false) }
    var decoding by remember { mutableStateOf(false) }
    var torchOn by remember { mutableStateOf(false) }
    var torchAvailable by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(ScanHistoryStore.all(context)) }
    var lastPayload by remember { mutableStateOf<String?>(null) }
    var cameraOn by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> cameraOn = granted }

    fun acceptHit(text: String, fmt: String, fromCamera: Boolean) {
        payload = text
        format = fmt
        status = context.getString(
            if (fromCamera) R.string.scan_ok_camera else R.string.scan_ok_gallery,
        )
        ScanHistoryStore.push(context, text, fmt)
        history = ScanHistoryStore.all(context)
        if (fromCamera && text != lastPayload) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        lastPayload = text
    }

    fun decodePickedUri(uri: Uri) {
        if (decoding) return
        decoding = true
        status = context.getString(R.string.scan_decoding)
        scope.launch {
            val hit = withContext(Dispatchers.Default) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use(QrDecoder::decodePhotoHit)
                }.getOrNull()
            }
            decoding = false
            if (hit != null) {
                acceptHit(hit.text, hit.format, fromCamera = false)
            } else {
                status = context.getString(R.string.scan_fail)
            }
        }
    }
    val gallery = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) decodePickedUri(uri)
    }
    val galleryFallback = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) decodePickedUri(uri)
    }
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.scan_title), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(R.string.scan_hint_point), style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
                        gallery.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    } else {
                        galleryFallback.launch("image/*")
                    }
                },
                enabled = !decoding,
            ) {
                Text(stringResource(R.string.scan_pick_gallery))
            }
            TextButton(onClick = { showHistory = !showHistory }) {
                Text(stringResource(R.string.scan_history))
            }
        }
        if (showHistory) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (history.isEmpty()) {
                    Text(stringResource(R.string.scan_history_empty))
                } else {
                    TextButton(onClick = {
                        ScanHistoryStore.clear(context)
                        history = emptyList()
                    }) {
                        Text(stringResource(R.string.scan_history_clear))
                    }
                    history.forEach { entry ->
                        Text(
                            text = "${entry.format}: ${entry.text.take(80)}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    acceptHit(entry.text, entry.format, fromCamera = false)
                                    showHistory = false
                                }
                                .padding(vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
        if (cameraOn) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { torchOn = !torchOn },
                    enabled = torchAvailable,
                ) {
                    Text(
                        stringResource(
                            if (torchOn) R.string.scan_torch_off else R.string.scan_torch_on,
                        ),
                    )
                }
            }
            ScanCameraPreview(
                modifier = Modifier.fillMaxWidth().weight(1f),
                torchOn = torchOn,
                onTorchAvailable = { torchAvailable = it },
                onHit = { hit -> acceptHit(hit.text, hit.format, fromCamera = true) },
            )
        } else {
            Text(stringResource(R.string.scan_camera_needed))
            Button(onClick = { permission.launch(Manifest.permission.CAMERA) }) {
                Text(stringResource(R.string.scan_grant_camera))
            }
        }
        if (status.isNotEmpty()) Text(status)
        val current = payload
        if (current != null) {
            Text(
                text = stringResource(R.string.scan_format, format),
                style = MaterialTheme.typography.labelMedium,
            )
            SelectionContainer {
                Text(
                    text = current,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                )
            }
            ScanActionRow(
                payload = current,
                saving = saving,
                onSave = {
                    if (saving) return@ScanActionRow
                    saving = true
                    scope.launch {
                        val saved = GalleryScanClone.save(context, repo, current)
                        saving = false
                        status = if (saved != null) {
                            context.getString(R.string.scan_saved_gallery)
                        } else {
                            context.getString(R.string.scan_save_failed)
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun ScanActionRow(
    payload: String,
    saving: Boolean,
    onSave: () -> Unit,
) {
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { ScanActions.copy(context, payload) }) {
            Text(stringResource(R.string.scan_copy))
        }
        Button(onClick = onSave, enabled = !saving) {
            Text(stringResource(R.string.scan_save_gallery))
        }
        when (ScanActions.kind(payload)) {
            ScanActionKind.Open -> Button(onClick = { ScanActions.open(context, payload) }) {
                Text(stringResource(R.string.scan_open))
            }
            ScanActionKind.JoinWifi -> Button(onClick = { ScanActions.joinWifi(context, payload) }) {
                Text(stringResource(R.string.scan_join_wifi))
            }
            else -> Unit
        }
    }
}
