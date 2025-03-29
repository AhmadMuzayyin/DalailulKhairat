package com.ustdev.dalailulkhairat

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsManager: SettingsManager
    private lateinit var volumeKeysSwitch: SwitchMaterial
    private lateinit var pageFlipSoundSwitch: SwitchMaterial
    private lateinit var pageFlipSoundStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsManager = SettingsManager(this)
        initViews()
        setupToolbar()
        loadSettings()
        setupListeners()
        volumeKeysSwitch = findViewById(R.id.volumeKeysSwitch)
        volumeKeysSwitch.isChecked = settingsManager.useVolumeKeys
        volumeKeysSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.useVolumeKeys = isChecked
        }
    }

    private fun initViews() {
        volumeKeysSwitch = findViewById(R.id.volumeKeysSwitch)
        pageFlipSoundSwitch = findViewById(R.id.pageFlipSoundSwitch)
        pageFlipSoundStatus = findViewById(R.id.pageFlipSoundStatus)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.settingsToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun loadSettings() {
        volumeKeysSwitch.isChecked = settingsManager.useVolumeKeys
        pageFlipSoundSwitch.isChecked = settingsManager.flipSound
        updatePageFlipSoundStatus(settingsManager.flipSound)
    }

    private fun setupListeners() {
        volumeKeysSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.useVolumeKeys = isChecked
        }

        pageFlipSoundSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.flipSound = isChecked
            updatePageFlipSoundStatus(isChecked)
        }
    }
    private fun updatePageFlipSoundStatus(isEnabled: Boolean) {
        pageFlipSoundStatus.text = if (isEnabled) "Page flip sound on" else "Page flip sound off"
    }
}