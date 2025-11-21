package com.ustdev.dalailulkhairat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModeSelectionActivity : AppCompatActivity() {
    
    private lateinit var imageModeCv: MaterialCardView
    private lateinit var textModeCv: MaterialCardView
    private lateinit var downloadProgress: ProgressBar
    private lateinit var downloadStatusTv: TextView
    private lateinit var useDefaultContentButton: Button
    private lateinit var settingsManager: SettingsManager
    private lateinit var contentDownloader: ContentDownloader
    
    private val TAG = "ModeSelectionActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)
        
        settingsManager = SettingsManager(this)
        contentDownloader = ContentDownloader(this)

        if (!settingsManager.isFirstLaunch) {
            settingsManager.isFirstLaunch = true
        }
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        imageModeCv = findViewById(R.id.imageModeCv)
        textModeCv = findViewById(R.id.textModeCv)
        downloadProgress = findViewById(R.id.downloadProgress)
        downloadStatusTv = findViewById(R.id.downloadStatusTv)
        useDefaultContentButton = findViewById(R.id.useDefaultContentBtn)
    }
    
    private fun setupListeners() {
        imageModeCv.setOnClickListener {
            showDownloadConfirmation(SettingsManager.MODE_IMAGE)
        }
        
        textModeCv.setOnClickListener {
            showDownloadConfirmation(SettingsManager.MODE_TEXT)
        }
        
        useDefaultContentButton.setOnClickListener {
            useDefaultContent()
        }
    }
    
    private fun showDownloadConfirmation(mode: String) {
        val contentType = if (mode == SettingsManager.MODE_IMAGE) "image" else "text"
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Unduh Konten")
            .setMessage("Ingin mengunduh konten $contentType? Ini mungkin menggunakan data seluler jika Anda tidak terhubung ke Wi-Fi..")
            .setPositiveButton("Unduh") { _, _ ->
                selectMode(mode)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun selectMode(mode: String) {
        settingsManager.displayMode = mode

        imageModeCv.isEnabled = false
        textModeCv.isEnabled = false
        useDefaultContentButton.isEnabled = false
        
        downloadProgress.visibility = View.VISIBLE
        downloadStatusTv.visibility = View.VISIBLE
        
        val contentType = if (mode == SettingsManager.MODE_IMAGE) "image" else "text"
        downloadStatusTv.text = "Mengunduh konten $contentType..."
        
        lifecycleScope.launch {
            try {
                val progressCallback: (Int) -> Unit = { progress ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        downloadProgress.progress = progress
                    }
                }

                val success = if (mode == SettingsManager.MODE_IMAGE) {
                    contentDownloader.downloadImageContent(progressCallback)
                } else {
                    contentDownloader.downloadTextContent(progressCallback)
                }

                if (success) {
                    if (mode == SettingsManager.MODE_IMAGE) {
                        settingsManager.isImageContentDownloaded = true
                    } else {
                        settingsManager.isTextContentDownloaded = true
                    }
                    
                    settingsManager.isFirstLaunch = false
                    
                    Toast.makeText(this@ModeSelectionActivity, "Konten berhasil diunduh", Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this@ModeSelectionActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    downloadStatusTv.text = "Unduhan gagal. Silakan coba lagi atau gunakan konten default."
                    imageModeCv.isEnabled = true
                    textModeCv.isEnabled = true
                    useDefaultContentButton.isEnabled = true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    downloadStatusTv.text = "Kesalahan: ${e.message}. Silakan coba lagi."
                    imageModeCv.isEnabled = true
                    textModeCv.isEnabled = true
                    useDefaultContentButton.isEnabled = true
                }
            }
        }
    }
    
    private fun useDefaultContent() {
        settingsManager.displayMode = SettingsManager.MODE_IMAGE
        settingsManager.isImageContentDownloaded = true
        settingsManager.isFirstLaunch = false
        
        Toast.makeText(this, "Menggunakan konten default", Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}