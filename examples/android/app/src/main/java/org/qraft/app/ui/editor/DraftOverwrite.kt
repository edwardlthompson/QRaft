package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.qraft.app.R
import org.qraft.app.editor.DraftDirty
import org.qraft.app.editor.EditorDraft
import org.qraft.app.ui.theme.MinTouchDp

@Composable
fun rememberDraftOverwrite(
    draft: EditorDraft,
    savedPayloads: List<String>,
    onSeed: (EditorDraft, String, String) -> Unit,
): (EditorDraft, String, String) -> Unit {
    var pending by remember { mutableStateOf<Triple<EditorDraft, String, String>?>(null) }
    pending?.let { seed ->
        DraftConfirmDialog(
            onKeep = { pending = null },
            onReplace = {
                onSeed(seed.first, seed.second, seed.third)
                pending = null
            },
        )
    }
    return { next, styleJson, name ->
        if (DraftDirty.isDirty(draft, savedPayloads)) {
            pending = Triple(next, styleJson, name)
        } else {
            onSeed(next, styleJson, name)
        }
    }
}

@Composable
fun DraftConfirmDialog(
    onKeep: () -> Unit,
    onReplace: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onKeep,
        title = { Text(stringResource(R.string.editor_replace_draft_title)) },
        text = { Text(stringResource(R.string.editor_replace_draft_body)) },
        confirmButton = {
            TextButton(onClick = onReplace, modifier = Modifier.heightIn(min = MinTouchDp)) {
                Text(stringResource(R.string.editor_replace_draft_replace))
            }
        },
        dismissButton = {
            TextButton(onClick = onKeep, modifier = Modifier.heightIn(min = MinTouchDp)) {
                Text(stringResource(R.string.editor_replace_draft_keep))
            }
        },
    )
}
