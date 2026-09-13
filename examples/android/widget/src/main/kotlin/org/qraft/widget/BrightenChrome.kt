package org.qraft.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button

internal object BrightenChrome {
    fun actionButton(context: Context, label: String, onClick: () -> Unit): Button =
        Button(context).apply { text = label; setOnClickListener { onClick() } }

    fun copy(context: Context, payload: String) {
        val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip.setPrimaryClip(ClipData.newPlainText("qr", payload))
    }

    fun openUrl(context: Context, payload: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(payload)))
    }

    fun haptic(context: Context) {
        val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
        if (Build.VERSION.SDK_INT >= 31) {
            val vm = context.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(effect)
        }
    }
}
