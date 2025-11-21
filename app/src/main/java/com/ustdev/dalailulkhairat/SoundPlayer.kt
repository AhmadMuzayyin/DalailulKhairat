package com.ustdev.dalailulkhairat

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class SoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val settingsManager = SettingsManager(context)

    fun playFlipSound() {
        if (!settingsManager.flipSound) {
            return
        }

        try {
            releaseMediaPlayer()
            val assetPath = "flipsound.mp3"
            mediaPlayer = MediaPlayer().apply {
                context.assets.openFd(assetPath).use { afd ->
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                }
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnErrorListener { _, what, extra ->
                    false
                }
                setOnCompletionListener {
                    releaseMediaPlayer()
                }
                prepareAsync()
            }

        } catch (e: Exception) {
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
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        releaseMediaPlayer()
    }
}