package com.example.composeapp

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("search")
    suspend fun fetchData(@Query("query") query: String): List<NewsProfile>
}