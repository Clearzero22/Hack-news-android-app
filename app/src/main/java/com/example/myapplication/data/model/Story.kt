package com.example.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class Story(
    val id: Int,
    val title: String,
    val url: String? = null,
    val score: Int = 0,
    @SerializedName("by")
    val author: String = "",
    @SerializedName("time")
    val timestamp: Long = 0,
    val descendants: Int = 0,
    val text: String? = null,
    val type: String = "story"
) {
    fun getTimeAgo(): String {
        val now = System.currentTimeMillis() / 1000
        val diff = now - timestamp
        
        return when {
            diff < 60 -> "${diff}s ago"
            diff < 3600 -> "${diff / 60}m ago"
            diff < 86400 -> "${diff / 3600}h ago"
            else -> "${diff / 86400}d ago"
        }
    }
    
    fun getDomain(): String? {
        return url?.let {
            try {
                val uri = java.net.URI(it)
                uri.host?.replace("www.", "")
            } catch (e: Exception) {
                null
            }
        }
    }
}