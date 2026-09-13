package org.qraft.widget

import android.view.View
import android.widget.RadioButton
import org.qraft.data.QrProfile

internal object WidgetConfigRadios {
    fun galleryRadio(
        activity: WidgetConfigActivity,
        profile: QrProfile,
        preselect: Boolean,
    ): RadioButton = RadioButton(activity).apply {
        id = View.generateViewId()
        text = buildString {
            append(profile.name)
            val hint = profile.payloadText.lineSequence().firstOrNull().orEmpty().take(42)
            if (hint.isNotBlank() && hint != profile.name) {
                append('\n')
                append(hint)
            }
        }
        tag = profile.id
        isChecked = preselect
    }
}
