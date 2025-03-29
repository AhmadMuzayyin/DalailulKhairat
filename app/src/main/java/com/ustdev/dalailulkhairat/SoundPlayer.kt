package com.ustdev.dalailulkhairat

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class SoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val settingsManager = SettingsManager(context)

    fun playFlipSound() {
        if (!settingsManager.flipSound) {
            Log.d("SoundPlayer", "Sound is disabled in settings")
            return
        }

        try {
            releaseMediaPlayer()

            // List semua file di assets untuk debugging
            context.assets.list("")?.forEach {
                Log.d("SoundPlayer", "Asset root: $it")
            }
            context.assets.list("style")?.forEach {
                Log.d("SoundPlayer", "Asset style/: $it")
            }
            context.assets.list("style/raw")?.forEach {
                Log.d("SoundPlayer", "Asset style/raw/: $it")
            }

            // Load dan play sound
            val assetPath = "style/raw/flipsound.mp3" // atau flipsound.ogg sesuai file yang ada
            mediaPlayer = MediaPlayer().apply {
                context.assets.openFd(assetPath).use { afd ->
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                }
                setOnPreparedListener { mp ->
                    mp.start()
                    Log.d("SoundPlayer", "Sound started playing")
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("SoundPlayer", "MediaPlayer Error: $what, $extra")
                    false
                }
                setOnCompletionListener {
                    Log.d("SoundPlayer", "Sound finished playing")
                    releaseMediaPlayer()
                }
                prepareAsync()
            }

        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error playing sound", e)
            e.printStackTrace()
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
                Log.d("SoundPlayer", "MediaPlayer released")
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error releasing MediaPlayer", e)
        }
    }

    fun release() {
        releaseMediaPlayer()
    }
}