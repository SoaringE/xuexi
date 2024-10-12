package com.example.composeapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login")
    fun postEmailPassword(@Body loginInfo: LoginRequest): Call<LoginResponse>
}