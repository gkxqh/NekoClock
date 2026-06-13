package com.gkxqh.nekoclock.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class ClockSettings(
    val showSeconds: Boolean,
    val showDate: Boolean,
    val fontSize: Float,
    val brightness: Float,
    val useChineseWeekday: Boolean,
    val dateFormat: String,
    val showBattery: Boolean,
    val burnInProtection: Boolean,
    val textColor: Color,
    val fontStyle: String,
    val wallpaperType: String,
    val wallpaperPath: String,
    val hideSettingsIcon: Boolean,
    val useBlur: Boolean,
    val useGlow: Boolean
)

class SettingsManager(private val context: Context) {
    object Keys {
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val FONT_SIZE = floatPreferencesKey("font_size")
        val BRIGHTNESS = floatPreferencesKey("brightness")
        val USE_CHINESE_WEEKDAY = booleanPreferencesKey("use_chinese_weekday")
        val DATE_FORMAT = stringPreferencesKey("date_format")
        val SHOW_BATTERY = booleanPreferencesKey("show_battery")
        val BURN_IN_PROTECTION = booleanPreferencesKey("burn_in_protection")
        val TEXT_COLOR = longPreferencesKey("text_color")
        val FONT_STYLE = stringPreferencesKey("font_style")
        val WALLPAPER_TYPE = stringPreferencesKey("wallpaper_type")
        val WALLPAPER_PATH = stringPreferencesKey("wallpaper_path")
        val HIDE_SETTINGS_ICON = booleanPreferencesKey("hide_settings_icon")
        val USE_BLUR = booleanPreferencesKey("use_blur")
        val USE_GLOW = booleanPreferencesKey("use_glow")
    }

    val settingsFlow: Flow<ClockSettings> = context.dataStore.data.map { prefs ->
        ClockSettings(
            showSeconds = prefs[Keys.SHOW_SECONDS] ?: false,
            showDate = prefs[Keys.SHOW_DATE] ?: true,
            fontSize = prefs[Keys.FONT_SIZE] ?: 80f,
            brightness = prefs[Keys.BRIGHTNESS] ?: 0.7f,
            useChineseWeekday = prefs[Keys.USE_CHINESE_WEEKDAY] ?: true,
            dateFormat = prefs[Keys.DATE_FORMAT] ?: "yyyy/MM/dd",
            showBattery = prefs[Keys.SHOW_BATTERY] ?: true,
            burnInProtection = prefs[Keys.BURN_IN_PROTECTION] ?: true,
            textColor = Color(prefs[Keys.TEXT_COLOR] ?: 0xFF4CAF50),
            fontStyle = prefs[Keys.FONT_STYLE] ?: "Monospace",
            wallpaperType = prefs[Keys.WALLPAPER_TYPE] ?: "none",
            wallpaperPath = prefs[Keys.WALLPAPER_PATH] ?: "",
            hideSettingsIcon = prefs[Keys.HIDE_SETTINGS_ICON] ?: false,
            useBlur = prefs[Keys.USE_BLUR] ?: false,
            useGlow = prefs[Keys.USE_GLOW] ?: false
        )
    }

    suspend fun <T> updateSetting(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { it[key] = value }
    }
}
