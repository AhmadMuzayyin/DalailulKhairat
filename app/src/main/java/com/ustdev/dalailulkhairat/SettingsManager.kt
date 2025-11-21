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
        
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
        
    var displayMode: String
        get() = prefs.getString(KEY_DISPLAY_MODE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DISPLAY_MODE, value).apply()
        
    var isImageContentDownloaded: Boolean
        get() = prefs.getBoolean(KEY_IMAGE_CONTENT_DOWNLOADED, false)
        set(value) = prefs.edit().putBoolean(KEY_IMAGE_CONTENT_DOWNLOADED, value).apply()
        
    var isTextContentDownloaded: Boolean
        get() = prefs.getBoolean(KEY_TEXT_CONTENT_DOWNLOADED, false)
        set(value) = prefs.edit().putBoolean(KEY_TEXT_CONTENT_DOWNLOADED, value).apply()
        
    var keepScreenOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_SCREEN_ON, false)
        set(value) = prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, value).apply()

    companion object {
        private const val PREFS_NAME = "dalailul_khairat_settings"
        private const val FLIP_SOUND_KEY  = "flip_sound"
        private const val KEY_LAST_READ_PAGE = "last_read_page"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DISPLAY_MODE = "display_mode"
        private const val KEY_IMAGE_CONTENT_DOWNLOADED = "image_content_downloaded"
        private const val KEY_TEXT_CONTENT_DOWNLOADED = "text_content_downloaded"
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        
        const val MODE_IMAGE = "mode_image"
        const val MODE_TEXT = "mode_text"
    }
}