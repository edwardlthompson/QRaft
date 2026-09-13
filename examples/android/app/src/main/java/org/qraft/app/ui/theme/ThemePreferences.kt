package org.qraft.app.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")

class ThemePreferences(private val context: Context) {
    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { prefs ->
        when (prefs[THEME_MODE_KEY]) {
            ThemeMode.Light.name -> ThemeMode.Light
            ThemeMode.Dark.name -> ThemeMode.Dark
            else -> ThemeMode.System
        }
    }

    /** Opt-in Material You; off by default for FOSS predictability. */
    val dynamicColor: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[DYNAMIC_COLOR_KEY] ?: false
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DYNAMIC_COLOR_KEY] = enabled
        }
    }
}
