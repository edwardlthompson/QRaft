package org.qraft.app.network

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * QRaft is offline-only (no INTERNET permission). Always reports offline so
 * About/update prompts never attempt network I/O.
 */
class NetworkStatusMonitor(@Suppress("UNUSED_PARAMETER") context: Context) {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun start() {
        _isOnline.value = false
    }

    fun stop() {
        // no-op
    }
}
