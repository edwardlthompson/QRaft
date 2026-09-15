package org.qraft.app.editor

import org.qraft.render.FinderShape
import org.qraft.render.QrFrame
import org.qraft.render.QrStyle

enum class EditorStep { Content, Look, Place, Share }

object EditorSteps {
    const val PREVIEW_COMPACT_DP = 120f
    const val PREVIEW_FULL_DP = 240f

    val FREQUENT = listOf(
        PayloadKind.Url,
        PayloadKind.Wifi,
        PayloadKind.Text,
        PayloadKind.VCard,
    )

    val CHIPS = listOf(EditorStep.Content, EditorStep.Look, EditorStep.Place)

    const val SHOW_CHIPS = false

    fun shown(step: EditorStep): EditorStep =
        if (step == EditorStep.Share) EditorStep.Place else step

    fun next(step: EditorStep): EditorStep = when (shown(step)) {
        EditorStep.Content -> EditorStep.Look
        EditorStep.Look -> EditorStep.Place
        else -> EditorStep.Place
    }

    fun previous(step: EditorStep): EditorStep = when (shown(step)) {
        EditorStep.Content -> EditorStep.Content
        EditorStep.Look -> EditorStep.Content
        else -> EditorStep.Look
    }

    fun consumeBack(step: EditorStep): EditorStep? =
        if (shown(step) == EditorStep.Content) null else previous(step)

    fun showMoreTypes(kind: PayloadKind): Boolean = kind !in FREQUENT

    fun showEcc(step: EditorStep, overlay: Boolean): Boolean =
        shown(step) == EditorStep.Look && overlay

    fun isAdvanced(style: QrStyle): Boolean {
        val d = QrStyle.DEFAULT
        return style.finderShape != FinderShape.SQUARE ||
            style.finderPupil != FinderShape.SQUARE ||
            style.eyeColorArgb != d.eyeColorArgb ||
            style.quietZoneModules != d.quietZoneModules ||
            style.frame != QrFrame.NONE ||
            style.cornerBadge ||
            style.gradient.enabled
    }
}
