package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.data.model.Story
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

/**
 * 优化版 Hacker News Repository
 * 使用并行请求和简单缓存提升性能
 */
class HackerNewsRepository {
    private val api = ApiClient.instance
    
    // 简单内存缓存
    private val storyCache = mutableMapOf<Int, Story>()
    
    suspend fun getTopStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getTopStories().take(limit)
            val stories = loadStoriesParallel(storyIds)
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNewStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getNewStories().take(limit)
            val stories = loadStoriesParallel(storyIds)
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBestStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getBestStories().take(limit)
            val stories = loadStoriesParallel(storyIds)
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 并行加载多个新闻，大幅提升性能
     */
    private suspend fun loadStoriesParallel(ids: List<Int>): List<Story> {
        return coroutineScope {
            ids.map { id ->
                async {
                    // 先检查缓存
                    storyCache[id] ?: run {
                        try {
                            val story = api.getStory(id)
                            // 只缓存有效的新闻
                            if (story.url != null) {
                                storyCache[id] = story
                            }
                            story
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
            }.awaitAll().filterNotNull().filter { it.url != null }
        }
    }
    
    suspend fun getStory(id: Int): Result<Story> {
        return try {
            // 先检查缓存
            storyCache[id]?.let { cachedStory ->
                return Result.success(cachedStory)
            }
            
            // 缓存未命中，从网络获取
            val story = api.getStory(id)
            storyCache[id] = story
            Result.success(story)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 清空缓存
     */
    fun clearCache() {
        storyCache.clear()
    }
}