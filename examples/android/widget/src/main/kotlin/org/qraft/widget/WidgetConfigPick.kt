package org.qraft.widget

internal object WidgetConfigPick {
    fun showPickError(selectedId: String?): Boolean = selectedId == null
}
