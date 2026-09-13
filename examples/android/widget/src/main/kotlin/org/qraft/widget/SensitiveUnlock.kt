package org.qraft.widget

/** When a gallery card is marked sensitive and the widget lock is on, reveal needs auth. */
object SensitiveUnlock {
    fun requiresAuth(profileSensitive: Boolean, lockEnabled: Boolean): Boolean =
        profileSensitive && lockEnabled
}
