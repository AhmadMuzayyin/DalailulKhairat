package com.ustdev.dalailulkhairat

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class ContentDownloader(private val context: Context) {
    
    private val TAG = "ContentDownloader"
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    companion object {
        private const val IMAGE_CONTENT_URL = "https://github.com/AhmadMuzayyin/DalailulKhairat/releases/download/image/image-content.zip"
        private const val TEXT_CONTENT_URL = ""
        
        const val IMAGE_DIR = "image"
        const val TEXT_DIR = "text"
    }
    
    suspend fun downloadImageContent(progressCallback: (Int) -> Unit): Boolean {
        return downloadContent(IMAGE_CONTENT_URL, IMAGE_DIR, progressCallback)
    }
    
    suspend fun downloadTextContent(progressCallback: (Int) -> Unit): Boolean {
        return downloadContent(TEXT_CONTENT_URL, TEXT_DIR, progressCallback)
    }
    
    private suspend fun downloadContent(url: String, subDir: String, progressCallback: (Int) -> Unit): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                var actualUrl = url
                
                val request = Request.Builder()
                    .url(actualUrl)
                    .build()
                
                val response = client.newCall(request).execute()
                
                if (!response.isSuccessful) {
                    return@withContext false
                }
                
                val contentLength = response.body?.contentLength() ?: -1L
                val inputStream = response.body?.byteStream() 
                if (inputStream == null) {
                    Log.e(TAG, "Response body or input stream is null")
                    return@withContext false
                }
                
                val contentDir = getContentDir(subDir)
                val tempFile = File(contentDir, "temp.zip")
                val outputStream = FileOutputStream(tempFile)
                val buffer = ByteArray(8192)
                var totalBytesRead = 0L
                var bytesRead: Int
                var lastProgressUpdate = 0L
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    
                    if (contentLength > 0 && (totalBytesRead - lastProgressUpdate > 100_000 || totalBytesRead >= contentLength)) {
                        val progress = (totalBytesRead * 100 / contentLength).toInt()
                        progressCallback(progress)
                        lastProgressUpdate = totalBytesRead
                    }
                }
                
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                val extracted = extractZipFile(tempFile, contentDir)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
                return@withContext extracted
            } catch (e: IOException) {
                return@withContext false
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }
    
    private fun getContentDir(subDir: String): File {
        val baseDir = context.getExternalFilesDir(null)
        val contentDir = File(baseDir, subDir)
        
        if (!contentDir.exists()) {
            contentDir.mkdirs()
        }
        
        return contentDir
    }
    
    private fun extractZipFile(zipFile: File, destinationDir: File): Boolean {
        try {
            val buffer = ByteArray(8192)
            val zip = java.util.zip.ZipFile(zipFile)
            val entries = zip.entries()
            
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val entryDestination = File(destinationDir, entry.name)
                
                if (entry.isDirectory) {
                    entryDestination.mkdirs()
                } else {
                    entryDestination.parentFile?.mkdirs()
                    
                    val inputStream = zip.getInputStream(entry)
                    val outputStream = FileOutputStream(entryDestination)
                    
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    
                    outputStream.close()
                    inputStream.close()
                }
            }
            
            zip.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}