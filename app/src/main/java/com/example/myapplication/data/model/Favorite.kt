package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val id: Int,
    val title: String,
    val url: String?,
    val author: String,
    val timestamp: Long,
    val score: Int,
    val commentCount: Int,
    val favoriteTime: Long = System.currentTimeMillis()
)