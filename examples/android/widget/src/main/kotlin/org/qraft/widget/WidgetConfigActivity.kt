package org.qraft.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import org.qraft.data.DataStoreProfileRepository

class WidgetConfigActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        )
        setResult(RESULT_CANCELED)
        val repo = DataStoreProfileRepository(this)
        val profiles = runBlocking { repo.seedIfEmpty(); repo.all() }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }
        root.addView(TextView(this).apply { text = getString(R.string.widget_config_title); textSize = 20f })
        root.addView(TextView(this).apply {
            text = getString(R.string.widget_config_hint); setPadding(0, 16, 0, 8)
        })
        root.addView(TextView(this).apply { text = getString(WidgetPinHelper.homeRes); textSize = 12f })
        root.addView(TextView(this).apply {
            text = getString(WidgetPinHelper.keyguardRes); textSize = 12f; setPadding(0, 0, 0, 24)
        })
        val group = RadioGroup(this)
        val pending = WidgetPrefs.pendingId(this)
        val selected = pending ?: WidgetPrefs.selectedId(this, widgetId).takeIf {
            WidgetPrefs.hasBoundId(this, widgetId)
        }
        if (profiles.isEmpty()) {
            root.addView(TextView(this).apply { text = getString(R.string.widget_config_empty) })
        } else {
            profiles.forEach {
                group.addView(WidgetConfigRadios.galleryRadio(this, it, preselect = it.id == selected))
            }
        }
        root.addView(
            ScrollView(this).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
                addView(group)
            },
        )
        val caption = CheckBox(this).apply {
            text = getString(R.string.widget_caption)
            isChecked = WidgetPrefs.captionEnabled(this@WidgetConfigActivity, widgetId)
        }
        val carousel = CheckBox(this).apply {
            text = getString(R.string.widget_carousel)
            isChecked = WidgetPrefs.carouselEnabled(this@WidgetConfigActivity, widgetId)
        }
        val sensitive = CheckBox(this).apply {
            text = getString(R.string.widget_sensitive_lock)
            isChecked = WidgetPrefs.sensitiveLock(this@WidgetConfigActivity, widgetId)
        }
        val transparent = CheckBox(this).apply {
            text = getString(R.string.widget_transparent)
            isChecked = WidgetPrefs.transparentBg(this@WidgetConfigActivity, widgetId)
        }
        root.addView(caption); root.addView(carousel); root.addView(sensitive); root.addView(transparent)
        root.addView(
            Button(this).apply {
                text = getString(R.string.widget_save)
                setOnClickListener {
                    val id = group.findViewById<RadioButton>(group.checkedRadioButtonId)?.tag as? String
                    if (id == null) {
                        Toast.makeText(this@WidgetConfigActivity, R.string.widget_config_pick_required, Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    WidgetPrefs.setSelectedId(this@WidgetConfigActivity, id, widgetId)
                    WidgetPrefs.clearPendingId(this@WidgetConfigActivity)
                    WidgetPrefs.setCaptionEnabled(this@WidgetConfigActivity, caption.isChecked, widgetId)
                    WidgetPrefs.setCarouselEnabled(this@WidgetConfigActivity, carousel.isChecked, widgetId)
                    WidgetPrefs.setSensitiveLock(this@WidgetConfigActivity, sensitive.isChecked, widgetId)
                    WidgetPrefs.setTransparentBg(this@WidgetConfigActivity, transparent.isChecked, widgetId)
                    setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                    finish()
                }
            },
        )
        setContentView(root)
    }
}
