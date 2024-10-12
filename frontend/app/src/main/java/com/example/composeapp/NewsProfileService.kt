package com.example.composeapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NewsProfileService {
    @POST("news")
    fun postWantedNews(@Body requestInfo: NewsRequest): Call<NewsResponse>
}