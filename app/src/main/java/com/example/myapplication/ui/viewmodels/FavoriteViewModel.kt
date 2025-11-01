package com.example.myapplication.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FavoriteRepository
import com.example.myapplication.data.model.Favorite
import com.example.myapplication.data.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val favorites: List<Favorite> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val favoriteStatus: Map<Int, Boolean> = emptyMap()
)

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FavoriteRepository(application)
    
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getAllFavorites().collect { favorites ->
                    _uiState.value = _uiState.value.copy(
                        favorites = favorites,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load favorites: ${e.message}"
                )
            }
        }
    }
    
    fun toggleFavorite(story: Story) {
        viewModelScope.launch {
            try {
                val isFavorite = repository.toggleFavorite(story)
                updateFavoriteStatus(story.id, isFavorite)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to toggle favorite: ${e.message}"
                )
            }
        }
    }
    
    suspend fun checkFavoriteStatus(storyId: Int): Boolean {
        return try {
            repository.isFavorite(storyId)
        } catch (e: Exception) {
            false
        }
    }
    
    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            try {
                repository.deleteFavorite(favorite)
                updateFavoriteStatus(favorite.id, false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete favorite: ${e.message}"
                )
            }
        }
    }
    
    fun deleteFavoriteById(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteFavoriteById(id)
                updateFavoriteStatus(id, false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete favorite: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun updateFavoriteStatus(storyId: Int, isFavorite: Boolean) {
        val currentStatus = _uiState.value.favoriteStatus.toMutableMap()
        currentStatus[storyId] = isFavorite
        _uiState.value = _uiState.value.copy(favoriteStatus = currentStatus)
    }
}