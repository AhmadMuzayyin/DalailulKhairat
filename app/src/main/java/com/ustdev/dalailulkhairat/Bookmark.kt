package com.ustdev.dalailulkhairat

data class Bookmark(
    val id: Long = 0,
    val pageNumber: Int,
    val timestamp: Long = System.currentTimeMillis()
)