package com.example.myapplication.data.repository

import com.example.myapplication.data.api.ApiClient
import com.example.myapplication.data.model.Story

class HackerNewsRepository {
    private val api = ApiClient.instance
    
    suspend fun getTopStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getTopStories().take(limit)
            val stories = storyIds.mapNotNull { id ->
                try {
                    api.getStory(id)
                } catch (e: Exception) {
                    null
                }
            }.filter { it.url != null }
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNewStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getNewStories().take(limit)
            val stories = storyIds.mapNotNull { id ->
                try {
                    api.getStory(id)
                } catch (e: Exception) {
                    null
                }
            }.filter { it.url != null }
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBestStories(limit: Int = 30): Result<List<Story>> {
        return try {
            val storyIds = api.getBestStories().take(limit)
            val stories = storyIds.mapNotNull { id ->
                try {
                    api.getStory(id)
                } catch (e: Exception) {
                    null
                }
            }.filter { it.url != null }
            Result.success(stories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getStory(id: Int): Result<Story> {
        return try {
            val story = api.getStory(id)
            Result.success(story)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}