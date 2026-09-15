package org.qraft.app.ui.chrome

object ChromeSelected {
    fun stateDescription(open: Boolean, expanded: String, collapsed: String): String =
        if (open) expanded else collapsed
}
