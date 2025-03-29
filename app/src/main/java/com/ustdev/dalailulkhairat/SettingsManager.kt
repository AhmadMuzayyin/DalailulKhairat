package com.ustdev.dalailulkhairat

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val preferences = context.getSharedPreferences("App_settings", Context.MODE_PRIVATE)

    var useVolumeKeys: Boolean
        get() = preferences.getBoolean("useVolumeKeys", false)
        set(value) {
            preferences.edit().putBoolean("useVolumeKeys", value).apply()
        }
    var flipSound: Boolean
        get() = prefs.getBoolean(FLIP_SOUND_KEY, true)
        set(value) = prefs.edit().putBoolean(FLIP_SOUND_KEY, value).apply()

    var lastReadPage: Int
        get() = prefs.getInt(KEY_LAST_READ_PAGE, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_READ_PAGE, value).apply()

    companion object {
        private const val PREFS_NAME = "dalailul_khairat_settings"
        private const val FLIP_SOUND_KEY  = "flip_sound"
        private const val KEY_LAST_READ_PAGE = "last_read_page"
    }
}