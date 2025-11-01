package com.example.myapplication.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Story
import com.example.myapplication.data.repository.HackerNewsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val selectedStoryType: StoryType = StoryType.TOP,
    val isRefreshing: Boolean = false,
    val cacheSize: Int = 0
)

class NewsViewModel : ViewModel() {
    private val repository = HackerNewsRepository()
    
    // 各类型新闻的缓存
    private val topStoriesCache = mutableListOf<Story>()
    private val newStoriesCache = mutableListOf<Story>()
    private val bestStoriesCache = mutableListOf<Story>()
    
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    private var loadJob: Job? = null
    
    init {
        // 启动时立即加载
        loadStories(StoryType.TOP)
        
        // 预加载其他类型
        viewModelScope.launch {
            delay(500) // 稍微延迟，避免竞争
            preloadOtherTypes(StoryType.TOP)
        }
    }
    
    fun loadStories(type: StoryType, forceRefresh: Boolean = false) {
        // 取消之前的加载任务
        loadJob?.cancel()
        
        loadJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !forceRefresh,
                isRefreshing = forceRefresh,
                error = null,
                selectedStoryType = type
            )
            
            // 如果强制刷新，清空缓存
            if (forceRefresh) {
                repository.clearCache()
                when (type) {
                    StoryType.TOP -> topStoriesCache.clear()
                    StoryType.NEW -> newStoriesCache.clear()
                    StoryType.BEST -> bestStoriesCache.clear()
                }
            }
            
            try {
                val result = when (type) {
                    StoryType.TOP -> repository.getTopStories()
                    StoryType.NEW -> repository.getNewStories()
                    StoryType.BEST -> repository.getBestStories()
                }
                
                result.fold(
                    onSuccess = { stories ->
                        // 更新缓存
                        when (type) {
                            StoryType.TOP -> {
                                topStoriesCache.clear()
                                topStoriesCache.addAll(stories)
                            }
                            StoryType.NEW -> {
                                newStoriesCache.clear()
                                newStoriesCache.addAll(stories)
                            }
                            StoryType.BEST -> {
                                bestStoriesCache.clear()
                                bestStoriesCache.addAll(stories)
                            }
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            stories = stories,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            cacheSize = getCacheSize()
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message ?: "Failed to load stories"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = e.message ?: "Network error"
                )
            }
        }
    }
    
    private fun preloadOtherTypes(currentType: StoryType) {
        viewModelScope.launch {
            try {
                when (currentType) {
                    StoryType.TOP -> {
                        // 预加载 NEW 和 BEST
                        val newResult = repository.getNewStories()
                        newResult.fold(
                            onSuccess = { stories ->
                                newStoriesCache.clear()
                                newStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                        
                        delay(200) // 避免同时请求
                        
                        val bestResult = repository.getBestStories()
                        bestResult.fold(
                            onSuccess = { stories ->
                                bestStoriesCache.clear()
                                bestStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                    }
                    StoryType.NEW -> {
                        // 预加载 TOP 和 BEST
                        val topResult = repository.getTopStories()
                        topResult.fold(
                            onSuccess = { stories ->
                                topStoriesCache.clear()
                                topStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                        
                        delay(200)
                        
                        val bestResult = repository.getBestStories()
                        bestResult.fold(
                            onSuccess = { stories ->
                                bestStoriesCache.clear()
                                bestStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                    }
                    StoryType.BEST -> {
                        // 预加载 TOP 和 NEW
                        val topResult = repository.getTopStories()
                        topResult.fold(
                            onSuccess = { stories ->
                                topStoriesCache.clear()
                                topStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                        
                        delay(200)
                        
                        val newResult = repository.getNewStories()
                        newResult.fold(
                            onSuccess = { stories ->
                                newStoriesCache.clear()
                                newStoriesCache.addAll(stories)
                            },
                            onFailure = { /* 静默失败 */ }
                        )
                    }
                }
            } catch (e: Exception) {
                // 预加载失败不影响主流程
            }
        }
    }
    
    private fun getCurrentStories(type: StoryType): List<Story> {
        return when (type) {
            StoryType.TOP -> topStoriesCache
            StoryType.NEW -> newStoriesCache
            StoryType.BEST -> bestStoriesCache
        }
    }
    
    private fun getCacheSize(): Int {
        return topStoriesCache.size + newStoriesCache.size + bestStoriesCache.size
    }
    
    fun refreshStories() {
        loadStories(_uiState.value.selectedStoryType, forceRefresh = true)
    }
    
    fun selectStoryType(type: StoryType) {
        if (type != _uiState.value.selectedStoryType) {
            val cachedStories = getCurrentStories(type)
            if (cachedStories.isNotEmpty()) {
                // 立即显示缓存数据
                _uiState.value = _uiState.value.copy(
                    stories = cachedStories,
                    selectedStoryType = type,
                    isLoading = false,
                    error = null,
                    cacheSize = getCacheSize()
                )
            }
            
            // 后台更新数据
            loadStories(type)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        loadJob?.cancel()
    }
}