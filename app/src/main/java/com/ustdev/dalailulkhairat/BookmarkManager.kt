package com.ustdev.dalailulkhairat

import android.content.Context

class BookmarkManager(context: Context) {
    private val prefs = context.getSharedPreferences("BookmarkPrefs", Context.MODE_PRIVATE)
    private val bookmarkKey = "bookmarks"

    fun addBookmark(pageNumber: Int) {
        synchronized(this) {
            val bookmarks = getBookmarks().toMutableSet()
            bookmarks.add(pageNumber)
            prefs.edit()
                .putStringSet(bookmarkKey, bookmarks.map { it.toString() }.toSet())
                .apply()
        }
    }

    fun removeBookmark(pageNumber: Int) {
        synchronized(this) {
            val bookmarks = getBookmarks().toMutableSet()
            bookmarks.remove(pageNumber)
            prefs.edit()
                .putStringSet(bookmarkKey, bookmarks.map { it.toString() }.toSet())
                .apply()
        }
    }

    fun isPageBookmarked(pageNumber: Int): Boolean {
        return getBookmarks().contains(pageNumber)
    }

    fun getBookmarks(): Set<Int> {
        return prefs.getStringSet(bookmarkKey, setOf())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: setOf()
    }

    fun clearBookmarks() {
        prefs.edit().remove(bookmarkKey).apply()
    }
}