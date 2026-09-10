package org.qraft.widget

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import org.qraft.coreqr.QrEncoder
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer

class BrightenActivity : Activity() {
    private var previousBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousBrightness = window.attributes.screenBrightness
        val params = window.attributes
        params.screenBrightness = BrightenAction.WINDOW_BRIGHTNESS
        window.attributes = params
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        haptic()
        val payload = intent.getStringExtra(EXTRA_PAYLOAD).orEmpty()
        val styleJson = intent.getStringExtra(EXTRA_STYLE).orEmpty()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val image = ImageView(this).apply {
            contentDescription = getString(R.string.widget_brighten_cd)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            )
            setOnClickListener { finish() }
        }
        if (payload.isNotBlank()) {
            val matrix = QrEncoder.encodeText(payload)
            val bmp: Bitmap = StyledQrRenderer.render(matrix, 1024, QrStyleJson.decode(styleJson))
            image.setImageBitmap(bmp)
        }
        root.addView(image)
        root.addView(actionButton(getString(R.string.widget_copy)) { copy(payload) })
        if (payload.startsWith("http", ignoreCase = true)) {
            root.addView(actionButton(getString(R.string.widget_open)) { openUrl(payload) })
        }
        setContentView(root)
    }

    private fun actionButton(label: String, onClick: () -> Unit): Button =
        Button(this).apply { text = label; setOnClickListener { onClick() } }

    private fun copy(payload: String) {
        val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clip.setPrimaryClip(ClipData.newPlainText("qr", payload))
    }

    private fun openUrl(payload: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(payload)))
    }

    private fun haptic() {
        val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
        if (Build.VERSION.SDK_INT >= 31) {
            val vm = getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            (getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(effect)
        }
    }

    override fun onDestroy() {
        val params = window.attributes
        params.screenBrightness = previousBrightness
        window.attributes = params
        super.onDestroy()
    }

    companion object {
        const val EXTRA_PAYLOAD = "org.qraft.widget.PAYLOAD"
        const val EXTRA_STYLE = "org.qraft.widget.STYLE"
    }
}
