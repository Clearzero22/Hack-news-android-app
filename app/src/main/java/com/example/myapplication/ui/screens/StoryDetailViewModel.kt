package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Story
import com.example.myapplication.data.repository.HackerNewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoryDetailUiState(
    val story: Story? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val webViewUrl: String? = null
)

class StoryDetailViewModel : ViewModel() {
    private val repository = HackerNewsRepository()
    
    private val _uiState = MutableStateFlow(StoryDetailUiState())
    val uiState: StateFlow<StoryDetailUiState> = _uiState.asStateFlow()
    
    fun loadStory(storyId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )
            
            val result = repository.getStory(storyId)
            result.fold(
                onSuccess = { story ->
                    _uiState.value = _uiState.value.copy(
                        story = story,
                        isLoading = false,
                        error = null,
                        webViewUrl = story.url
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load story"
                    )
                }
            )
        }
    }
    
    fun openWebView() {
        _uiState.value = _uiState.value.copy(
            webViewUrl = _uiState.value.story?.url
        )
    }
    
    fun closeWebView() {
        _uiState.value = _uiState.value.copy(
            webViewUrl = null
        )
    }
    
    fun shareStory(): String {
        val story = _uiState.value.story
        return if (story != null) {
            "${story.title}\n${story.url ?: ""}\n\nShared via Hacker News App"
        } else {
            "Loading story..."
        }
    }
}