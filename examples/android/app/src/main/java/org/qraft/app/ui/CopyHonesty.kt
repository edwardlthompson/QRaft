package org.qraft.app.ui

/** When in-app copy should show, vs release chrome that must stay quiet. */
object CopyHonesty {
    fun showScanStatus(status: String, hint: String): Boolean =
        status.isNotBlank() && status != hint

    fun showAboutNavDebug(debugBuild: Boolean): Boolean = debugBuild
}
