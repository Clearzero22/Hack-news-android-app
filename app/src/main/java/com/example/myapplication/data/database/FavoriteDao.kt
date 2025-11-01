package com.example.myapplication.data.database

import androidx.room.*
import com.example.myapplication.data.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    
    @Query("SELECT * FROM favorites ORDER BY favoriteTime DESC")
    fun getAllFavorites(): Flow<List<Favorite>>
    
    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getFavoriteById(id: Int): Favorite?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)
    
    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
    
    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
    
    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
    
    @Query("SELECT COUNT(*) FROM favorites WHERE id = :id")
    suspend fun isFavorite(id: Int): Int
}