package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Story
import com.example.myapplication.data.repository.HackerNewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StoryType {
    TOP, NEW, BEST
}

data class NewsUiState(
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedStoryType: StoryType = StoryType.TOP
)

class NewsViewModel : ViewModel() {
    private val repository = HackerNewsRepository()
    
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    init {
        loadStories()
    }
    
    fun loadStories(type: StoryType = _uiState.value.selectedStoryType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedStoryType = type
            )
            
            val result = when (type) {
                StoryType.TOP -> repository.getTopStories()
                StoryType.NEW -> repository.getNewStories()
                StoryType.BEST -> repository.getBestStories()
            }
            
            result.fold(
                onSuccess = { stories ->
                    _uiState.value = _uiState.value.copy(
                        stories = stories,
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load stories"
                    )
                }
            )
        }
    }
    
    fun refreshStories() {
        loadStories(_uiState.value.selectedStoryType)
    }
    
    fun selectStoryType(type: StoryType) {
        if (type != _uiState.value.selectedStoryType) {
            loadStories(type)
        }
    }
}