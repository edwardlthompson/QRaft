package org.qraft.app.ui.product

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.gallery.ScanHomeSeed
import org.qraft.app.ui.scan.ScanScreen
import androidx.compose.material3.SnackbarHostState

@Composable
internal fun ProductScanPage(
    onSeedHome: (EditorDraft, String, String) -> Unit,
    onOpenHome: () -> Unit,
    onOpenGallery: () -> Unit,
    snackbarHostState: SnackbarHostState,
    context: Context,
    scope: CoroutineScope,
    refresh: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewLabel = context.getString(R.string.snack_view)
    val savedMsg = context.getString(R.string.scan_saved_gallery)
    ScanScreen(
        modifier = modifier,
        onSaved = {
            scope.launch { refresh() }
            scope.notice(snackbarHostState, savedMsg, viewLabel) { onOpenGallery() }
        },
        onSaveFailed = {
            scope.notice(snackbarHostState, context.getString(R.string.scan_save_failed))
        },
        onEditOnHome = { payload ->
            val seed = ScanHomeSeed.apply(payload)
            onSeedHome(seed.first, seed.second, seed.third)
            onOpenHome()
        },
    )
}
