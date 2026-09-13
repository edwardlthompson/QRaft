package org.qraft.app.ui.scan

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.qraft.scan.DecodeHit
import org.qraft.scan.QrDecoder
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Live preview via CameraX + ZXing on the Y plane (not ML Kit / Play Services).
 */
@Composable
fun ScanCameraPreview(
    modifier: Modifier,
    torchOn: Boolean,
    onTorchAvailable: (Boolean) -> Unit,
    onHit: (DecodeHit) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lastEmit = remember { AtomicLong(0L) }
    val analyzing = remember { AtomicBoolean(false) }
    val cameraRef = remember { AtomicReference<Camera?>(null) }
    var bound by remember { mutableStateOf(false) }

    LaunchedEffect(torchOn, bound) {
        val cam = cameraRef.get() ?: return@LaunchedEffect
        if (cam.cameraInfo.hasFlashUnit()) {
            runCatching { cam.cameraControl.enableTorch(torchOn) }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                    val future = ProcessCameraProvider.getInstance(ctx)
                    future.addListener(
                        {
                            runCatching {
                                val provider = future.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                                val analysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .setTargetResolution(android.util.Size(1280, 720))
                                    .build()
                                val executor = Executors.newSingleThreadExecutor()
                                analysis.setAnalyzer(executor) { image ->
                                    try {
                                        if (!analyzing.compareAndSet(false, true)) return@setAnalyzer
                                        try {
                                            val now = System.currentTimeMillis()
                                            if (now - lastEmit.get() < 450L) return@setAnalyzer
                                            val plane = image.planes[0]
                                            val hit = runCatching {
                                                QrDecoder.decodeYPlaneHit(
                                                    plane.buffer,
                                                    image.width,
                                                    image.height,
                                                    plane.rowStride,
                                                    image.imageInfo.rotationDegrees,
                                                )
                                            }.getOrNull() ?: return@setAnalyzer
                                            lastEmit.set(now)
                                            previewView.post { onHit(hit) }
                                        } finally {
                                            analyzing.set(false)
                                        }
                                    } finally {
                                        image.close()
                                    }
                                }
                                provider.unbindAll()
                                val camera = provider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analysis,
                                )
                                cameraRef.set(camera)
                                bound = true
                                val flash = camera.cameraInfo.hasFlashUnit()
                                previewView.post { onTorchAvailable(flash) }
                            }
                        },
                        ContextCompat.getMainExecutor(ctx),
                    )
                }
            },
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp),
                ),
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            runCatching { ProcessCameraProvider.getInstance(context).get().unbindAll() }
            cameraRef.set(null)
            bound = false
        }
    }
}
