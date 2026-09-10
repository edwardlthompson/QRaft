package org.qraft.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
        val profiles = runBlocking {
            repo.seedIfEmpty()
            repo.all()
        }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(TextView(this).apply { text = getString(R.string.widget_config_title) })
        val group = RadioGroup(this)
        val pending = WidgetPrefs.pendingId(this)
        val selected = pending ?: WidgetPrefs.selectedId(this, widgetId)
        profiles.forEach { profile ->
            val button = RadioButton(this).apply {
                id = View.generateViewId()
                text = profile.name
                tag = profile.id
                isChecked = profile.id == selected
            }
            group.addView(button)
        }
        root.addView(group)
        val caption = CheckBox(this).apply {
            text = getString(R.string.widget_caption)
            isChecked = WidgetPrefs.captionEnabled(this@WidgetConfigActivity)
        }
        val sensitive = CheckBox(this).apply {
            text = getString(R.string.widget_sensitive_lock)
            isChecked = WidgetPrefs.sensitiveLock(this@WidgetConfigActivity)
        }
        val transparent = CheckBox(this).apply {
            text = getString(R.string.widget_transparent)
            isChecked = WidgetPrefs.transparentBg(this@WidgetConfigActivity)
        }
        root.addView(caption)
        root.addView(sensitive)
        root.addView(transparent)
        root.addView(
            Button(this).apply {
                text = getString(R.string.widget_save)
                setOnClickListener {
                    val checked = group.findViewById<RadioButton>(group.checkedRadioButtonId)
                    val id = (checked?.tag as? String)
                        ?: WidgetPrefs.pendingId(this@WidgetConfigActivity)
                        ?: profiles.firstOrNull()?.id
                    if (id != null) WidgetPrefs.setSelectedId(this@WidgetConfigActivity, id, widgetId)
                    WidgetPrefs.setCaptionEnabled(this@WidgetConfigActivity, caption.isChecked)
                    WidgetPrefs.setSensitiveLock(this@WidgetConfigActivity, sensitive.isChecked)
                    WidgetPrefs.setTransparentBg(this@WidgetConfigActivity, transparent.isChecked)
                    val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    setResult(RESULT_OK, result)
                    finish()
                }
            },
        )
        setContentView(root)
    }
}
