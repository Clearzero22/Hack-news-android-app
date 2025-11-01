package com.example.myapplication.data.api

import com.example.myapplication.data.model.Story
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {
    
    @GET("topstories.json")
    suspend fun getTopStories(): List<Int>
    
    @GET("newstories.json")
    suspend fun getNewStories(): List<Int>
    
    @GET("beststories.json")
    suspend fun getBestStories(): List<Int>
    
    @GET("item/{id}.json")
    suspend fun getStory(@Path("id") id: Int): Story
}