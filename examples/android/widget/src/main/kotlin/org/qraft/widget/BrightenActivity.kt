package org.qraft.widget

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.runBlocking
import org.qraft.coreqr.QrEncoder
import org.qraft.data.DataStoreProfileRepository
import org.qraft.render.QrStyleJson
import org.qraft.render.StyledQrRenderer

/**
 * Full-screen bright QR. Sensitive profiles require device PIN / biometric first
 * and never carry payload in the launching Intent.
 */
class BrightenActivity : FragmentActivity() {
    private var previousBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        previousBrightness = window.attributes.screenBrightness
        val params = window.attributes
        params.screenBrightness = BrightenAction.WINDOW_BRIGHTNESS
        window.attributes = params
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        BrightenChrome.haptic(this)
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        setContentView(root)
        if (intent.getBooleanExtra(EXTRA_REQUIRES_AUTH, false)) {
            showLocked()
            BrightenAuth.prompt(this, onSuccess = { revealAfterAuth() }, onCancel = { finish() })
        } else {
            showQr(
                intent.getStringExtra(EXTRA_PAYLOAD).orEmpty(),
                intent.getStringExtra(EXTRA_STYLE).orEmpty(),
            )
        }
    }

    private fun showLocked() {
        root.removeAllViews()
        root.addView(
            TextView(this).apply {
                text = getString(R.string.widget_unlock_prompt)
                gravity = Gravity.CENTER
            },
        )
        root.addView(
            BrightenChrome.actionButton(this, getString(R.string.widget_unlock)) {
                BrightenAuth.prompt(this, onSuccess = { revealAfterAuth() }, onCancel = { finish() })
            },
        )
    }

    private fun revealAfterAuth() {
        val id = intent.getStringExtra(EXTRA_PROFILE_ID).orEmpty()
        val profile = runBlocking { DataStoreProfileRepository(this@BrightenActivity).get(id) }
            ?: DataStoreProfileRepository.seedWebsite()
        showQr(profile.payloadText, profile.styleJson)
    }

    private fun showQr(payload: String, styleJson: String) {
        root.removeAllViews()
        val style = QrStyleJson.decode(styleJson)
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
            // QR only as bitmap; caption is a real TextView so enlarge stays sharp.
            val bmp: Bitmap = StyledQrRenderer.render(matrix, 1024, style, applyCaption = false)
            image.setImageBitmap(bmp)
        }
        root.addView(image)
        val caption = style.caption.text.trim().ifBlank {
            intent.getStringExtra(EXTRA_CAPTION).orEmpty().trim()
        }
        if (caption.isNotEmpty()) {
            root.addView(
                TextView(this).apply {
                    text = caption
                    textSize = 22f
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(24, 16, 24, 8)
                },
            )
        }
        root.addView(
            BrightenChrome.actionButton(this, getString(R.string.widget_copy)) {
                BrightenChrome.copy(this, payload)
            },
        )
        if (payload.startsWith("http", ignoreCase = true)) {
            root.addView(
                BrightenChrome.actionButton(this, getString(R.string.widget_open)) {
                    BrightenChrome.openUrl(this, payload)
                },
            )
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
        const val EXTRA_CAPTION = "org.qraft.widget.CAPTION"
        const val EXTRA_PROFILE_ID = "org.qraft.widget.PROFILE_ID"
        const val EXTRA_REQUIRES_AUTH = "org.qraft.widget.REQUIRES_AUTH"
    }
}
