package org.qraft.app.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import org.qraft.app.R
import org.qraft.app.editor.EditorDraft
import org.qraft.app.editor.EditorSteps
import org.qraft.app.editor.PayloadKind
import org.qraft.app.ui.theme.MinTouchDp
import org.qraft.app.ui.theme.SpacingMd

@Composable
internal fun EditorStepContent(
    draft: EditorDraft,
    onDraftChange: (EditorDraft) -> Unit,
    validation: String?,
    onCurrentWifi: () -> Unit,
    onPasteClipboard: () -> Unit,
    moreOpen: Boolean,
    onToggleMore: () -> Unit,
) {
    val extra = PayloadKind.entries.filter { it !in EditorSteps.FREQUENT }
    val extraSelected = draft.kind.takeIf { it !in EditorSteps.FREQUENT }
    val moreExpanded = stringResource(R.string.editor_expanded)
    val moreCollapsed = stringResource(R.string.editor_collapsed)
    Column(verticalArrangement = Arrangement.spacedBy(SpacingMd), modifier = Modifier.fillMaxWidth()) {
        EditorSteps.FREQUENT.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingMd),
            ) {
                row.forEach { kind ->
                    KindChip(
                        kind = kind,
                        selected = draft.kind == kind,
                        onSelect = { onDraftChange(EditorDraft.withKind(kind)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (!moreOpen && extraSelected != null) {
            KindChip(
                kind = extraSelected,
                selected = true,
                onSelect = { onDraftChange(EditorDraft.withKind(extraSelected)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        TextButton(
            onClick = onToggleMore,
            modifier = Modifier.semantics {
                stateDescription = if (moreOpen) {
                    moreExpanded
                } else {
                    moreCollapsed
                }
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.editor_step_more))
                Icon(
                    if (moreOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                )
            }
        }
        if (moreOpen) {
            extra.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SpacingMd),
                ) {
                    row.forEach { kind ->
                        KindChip(
                            kind = kind,
                            selected = draft.kind == kind,
                            onSelect = { onDraftChange(EditorDraft.withKind(kind)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (row.size == 1) {
                        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        PayloadFields(draft = draft, onDraftChange = onDraftChange)
        if (validation != null) {
            Text(text = validation, color = MaterialTheme.colorScheme.error)
        }
        if (draft.kind == PayloadKind.Wifi) {
            TextButton(onClick = onCurrentWifi, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.editor_current_wifi))
            }
        }
        TextButton(onClick = onPasteClipboard, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.editor_paste_clipboard))
        }
    }
}

@Composable
private fun KindChip(
    kind: PayloadKind,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onSelect,
        label = { Text(stringResource(kindLabelRes(kind))) },
        leadingIcon = { Icon(PayloadKindIcons.of(kind), contentDescription = null) },
        modifier = modifier.heightIn(min = MinTouchDp),
    )
}
