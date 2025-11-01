package com.example.myapplication.data.database

import android.content.Context
import com.example.myapplication.data.model.Favorite
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(context: Context) {
    private val favoriteDao = AppDatabase.getDatabase(context).favoriteDao()
    
    fun getAllFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getAllFavorites()
    }
    
    suspend fun getFavoriteById(id: Int): Favorite? {
        return favoriteDao.getFavoriteById(id)
    }
    
    suspend fun isFavorite(id: Int): Boolean {
        return favoriteDao.isFavorite(id) > 0
    }
    
    suspend fun insertFavorite(favorite: Favorite) {
        favoriteDao.insertFavorite(favorite)
    }
    
    suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }
    
    suspend fun deleteFavoriteById(id: Int) {
        favoriteDao.deleteFavoriteById(id)
    }
    
    suspend fun deleteAllFavorites() {
        favoriteDao.deleteAllFavorites()
    }
    
    suspend fun toggleFavorite(story: com.example.myapplication.data.model.Story): Boolean {
        return if (isFavorite(story.id)) {
            deleteFavoriteById(story.id)
            false
        } else {
            val favorite = Favorite(
                id = story.id,
                title = story.title,
                url = story.url,
                author = story.author,
                timestamp = story.timestamp,
                score = story.score,
                commentCount = story.descendants
            )
            insertFavorite(favorite)
            true
        }
    }
}