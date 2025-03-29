package com.ustdev.dalailulkhairat

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var mainContent: LinearLayout
    private lateinit var webViewContainer: LinearLayout
    private lateinit var webView: WebView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var settingsManager: SettingsManager
    private lateinit var bookmarkManager: BookmarkManager
    private var isWebViewInitialized = false
    private var pendingPage = -1
    private var currentPage = 1
    private lateinit var soundPlayer: SoundPlayer
    private var isFullscreen = false


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize managers
        settingsManager = SettingsManager(this)
        bookmarkManager = BookmarkManager(this)
        soundPlayer = SoundPlayer(this)

        // Initialize UI after content check
        setContentView(R.layout.activity_main)
        initViews()
        setupCardClickListeners()
        setupToolbar()
        setupViewPager()
        setupWebView()

        // Inisialisasi theme manager
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }
    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        mainContent = findViewById(R.id.mainContent)
        webViewContainer = findViewById(R.id.webViewContainer)
        webView = findViewById(R.id.webView)
    }

    private fun setupCardClickListeners() {
        val lastReadCard = findViewById<MaterialCardView>(R.id.lastReadCard)
        val goToPageCard = findViewById<MaterialCardView>(R.id.goToPageCard)
        val settingsCard = findViewById<MaterialCardView>(R.id.settingsCard)

        lastReadCard.setOnClickListener {
            openLastReadPage()
        }

        goToPageCard.setOnClickListener {
            showGoToPageDialog()
        }

        settingsCard.setOnClickListener {
            try {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGoToPageDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_go_to_page, null)

        val pageInput = dialogView.findViewById<TextInputEditText>(R.id.pageInputEditText)

        val dialog = builder.setView(dialogView)
            .setTitle("Go to Page")
            .setPositiveButton("Go", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val pageStr = pageInput.text.toString()
            if (pageStr.isNotEmpty()) {
                try {
                    val page = pageStr.toInt()
                    when {
                        page < 1 -> showErrorDialog("Invalid Page", "Minimum page is 1")
                        page > 205 -> showErrorDialog("Invalid Page", "Maximum page is 205")
                        else -> {
                            showWebView(page)
                            dialog.dismiss()
                        }
                    }
                } catch (e: NumberFormatException) {
                    showErrorDialog("Invalid Input", "Please enter a valid page number")
                }
            } else {
                showErrorDialog("Empty Input", "Please enter a page number")
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_error)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Chapter"
                1 -> "Bookmark"
                else -> ""
            }
        }.attach()
    }

    private fun updateBookmarkIcon(pageNumber: Int) {
        val menuItem = toolbar.menu.findItem(R.id.action_bookmark)
        menuItem?.setIcon(
            if (bookmarkManager.isPageBookmarked(pageNumber)) {
                R.drawable.ic_star_filled
            } else {
                R.drawable.ic_star
            }
        )
    }

    private fun toggleBookmark(pageNumber: Int) {
        if (bookmarkManager.isPageBookmarked(pageNumber)) {
            bookmarkManager.removeBookmark(pageNumber)
            Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
        } else {
            bookmarkManager.addBookmark(pageNumber)
            Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
        }
        updateBookmarkIcon(pageNumber)
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true               // Tambahkan ini
            allowFileAccessFromFileURLs = true   // Tambahkan ini
            allowUniversalAccessFromFileURLs = true // Tambahkan ini
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun nextPage() {
                runOnUiThread {
                    webView.evaluateJavascript("nextPageFun()", null)
                }
            }

            @JavascriptInterface
            fun previousPage() {
                runOnUiThread {
                    webView.evaluateJavascript("previousPageFun()", null)
                }
            }

            @JavascriptInterface
            fun firstPage() {
                runOnUiThread {
                    webView.evaluateJavascript("firstPageFun()", null)
                }
            }

            @JavascriptInterface
            fun lastPage() {
                runOnUiThread {
                    webView.evaluateJavascript("lastPageFun()", null)
                }
            }
            @JavascriptInterface
            fun onPageChange() {
                runOnUiThread {
                    soundPlayer.playFlipSound()
                }
            }
        }, "AndroidBridge")
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("WebView", "Error: ${error?.description}, Code: ${error?.errorCode}")
            }
        }
    }

    fun showWebView(pageNumber: Int) {
        try {
            currentPage = pageNumber
            settingsManager.lastReadPage = pageNumber
            var number = pageNumber + 1

            toolbar.setNavigationIcon(R.drawable.ic_back)
            toolbar.menu.clear()
            toolbar.inflateMenu(R.menu.webview_menu)

            val menuItem = toolbar.menu.findItem(R.id.action_bookmark)
            menuItem?.setIcon(
                if (bookmarkManager.isPageBookmarked(currentPage))
                    R.drawable.ic_star_filled
                else
                    R.drawable.ic_star
            )

            toolbar.setNavigationOnClickListener {
                hideWebView()
            }

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_bookmark -> {
                        toggleBookmark(currentPage)
                        true
                    }
                    R.id.action_fullscreen -> {
                        toggleFullscreen()
                        true
                    }
                    else -> false
                }
            }

            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    url?.let {
                        try {
                            val pageNum = url.substringAfter("#p=").toInt() - 1
                            currentPage = pageNum
                            settingsManager.lastReadPage = pageNum
                            updateBookmarkIcon(currentPage)
                            soundPlayer.playFlipSound()
                        } catch (e: Exception) {
                            Log.e("WebView", "Error parsing page number: ${e.message}")
                        }
                    }
                }
            }

            // Load from local storage instead of assets
            webView.loadUrl("file:///android_asset/reader.html#p=$number")
            mainContent.visibility = View.GONE
            webViewContainer.visibility = View.VISIBLE
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to load content: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun hideWebView() {
        // Reset fullscreen mode
        if (isFullscreen) {
            isFullscreen = false
            setupWindowFlags()
        }

        // Reset toolbar
        toolbar.navigationIcon = null
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.top_menu)
        toolbar.setNavigationOnClickListener(null)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                else -> false
            }
        }

        // Clear WebView completely
        webView.apply {
            clearHistory()           // Tambahkan ini
            clearCache(true)         // Tambahkan ini
            clearFormData()          // Tambahkan ini
            clearSslPreferences()    // Tambahkan ini
            loadUrl("about:blank")
        }

        // Reset flags dan visibility
        isWebViewInitialized = false
        pendingPage = -1
        webViewContainer.visibility = View.GONE
        mainContent.visibility = View.VISIBLE
    }

    private fun openLastReadPage() {
        val lastPage = settingsManager.lastReadPage
        if (lastPage > 0) {
            showWebView(lastPage)
        } else {
            showErrorDialog("No Last Read Page", "You haven't read any pages yet")
        }
    }

    private fun setupWindowFlags() {
        if (isFullscreen) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            supportActionBar?.hide()
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            supportActionBar?.show()
        }
    }

    private fun toggleFullscreen() {
        isFullscreen = !isFullscreen
        setupWindowFlags()
        // Update icon
        toolbar.menu.findItem(R.id.action_fullscreen)?.setIcon(
            if (isFullscreen) R.drawable.ic_fullscreen_exit
            else R.drawable.ic_fullscreen
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (webViewContainer.visibility == View.VISIBLE && settingsManager.useVolumeKeys) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    webView.evaluateJavascript("previousPageFun()", null)
                    soundPlayer.playFlipSound()
                    return true
                }
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    webView.evaluateJavascript("nextPageFun()", null)
                    soundPlayer.playFlipSound()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        when {
            isFullscreen -> {
                isFullscreen = false
                setupWindowFlags()
                // Update menu icon
                toolbar.menu.findItem(R.id.action_fullscreen)?.setIcon(R.drawable.ic_fullscreen)
            }
            webViewContainer.visibility == View.VISIBLE -> {
                hideWebView()
                webView.clearHistory()
            }
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (webViewContainer.visibility == View.VISIBLE) {
            menuInflater.inflate(R.menu.webview_menu, menu)
        } else {
            menuInflater.inflate(R.menu.top_menu, menu)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        soundPlayer.release()
        super.onDestroy()
    }
}

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ChapterFragment()
            1 -> BookmarkFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}