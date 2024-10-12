package com.example.composeapp

import com.google.gson.annotations.SerializedName

data class NewsProfile(
    @SerializedName("ID")
    val id: Long,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Time")
    val time: String,
    @SerializedName("Link")
    val link: String
)
